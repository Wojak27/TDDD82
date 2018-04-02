<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Bus\DispatchesJobs;
use Illuminate\Routing\Controller as BaseController;
use Illuminate\Foundation\Validation\ValidatesRequests;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Foundation\Auth\Access\AuthorizesResources;
use Artisan;
use DB;

class Controller extends BaseController
{
    use AuthorizesRequests, AuthorizesResources, DispatchesJobs, ValidatesRequests;

   public function handleReplicationToBackup(){
	$this->incrementDefaultTime();
	$this->dumpDefaultDB();
	$this->restoreBackupDB();
   }

   public function handleReplicationToPrimary(){
	$this->incrementBackupTime();
	$this->dumpBackupDB();
	$this->restoreDefaultDB();
   }

   public function restoreDatabaseConstiency(){
	$default_time = $this->readDefaultTime();
	$backup_time = $this->readBackupTime();
	if($default_time > $backup_time){
		$this->restoreBackupDB();
		return;
	}
	if($backup_time > $default_time){
		$this->restoreDefaultDB();
		return;
	}
   }

   public function readDefaultTime(){
	return DB::connection('mysql')->select(
	'select timestamp from timestamps where id = 1'
	);
   }

   public function readBackupTime(){
	return DB::connection('mysql_backup')->select(
	'select timestamp from timestamps where id = 1'
	);
   }

    public function incrementDefaultTime(){
	DB::connection('mysql')->update(
	'update timestamps set timestamp = timestamp + 1 where id = 1'
	);
    }

    public function incrementBackupTime(){
	DB::connection('mysql_backup')->update(
	'update timestamps set timestamp = timestamp + 1 where id = 1'
	);

    }
     public function dumpBackupDB(){
	Artisan::call('backup:mysql-dump',
	[
	'filename' => 'jesperhej',
	'--backup' => true
	]);
     }

    public function dumpDefaultDB(){
	Artisan::call('backup:mysql-dump',
	[
	'filename' => 'jesperhej'
	]);
     }

     public function restoreBackupDB(){
	Artisan::call('backup:mysql-restore',
	[
	'--filename' => 'jesperhej.sql',
	'--yes' => true,
	'--backup' => true
	]);
     }
     public function restoreDefaultDB(){
	Artisan::call('backup:mysql-restore',
	[
	'--filename' => 'jesperhej.sql',
	'--yes' => true,
	]);
     }
}
