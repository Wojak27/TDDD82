<?php

namespace App\Http\Controllers;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\User;
use App\Message;
use App\BackupMessage;
use Artisan;

class MessageController extends Controller
{
     public function testBackup(){
	//$this->incrementDefaultTime();
	$this->incrementBackupTime();
	//$this->dumpBackupDB();
	//$this->dumpDefaultDB();
	//$this->restoreDefaultDB();
	//$this->switchToBackupDB();
	//$this->loadDBBackup();
	//return Artisan::output();
     }

     public function getAllSentMessages(Request $request){
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return User::find($user_id)->sentMessages;        
    }

     public function getAllReceivedMessages(Request $request){
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return User::find($user_id)->receivedMessages;        
    }

     public function getChatMessages(Request $request){
	$token = JWTAuth::getToken();
	$requester_user_id = strtoupper(JWTAuth::toUser($token)->id);
	$chat_partner_user_id = $request->get('chat_partner_id');
	$valid_ids = array($requester_user_id, $chat_partner_user_id);
	return DB::table('messages')
              ->whereIn('sender_id', $valid_ids)
              ->whereIn('receiver_id', $valid_ids)
              ->get();
	}
	

     public function sendMessage(Request $request){
	$this->restoreDatabaseConstiency();
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	$message = Message::create([
       'sender_id' => $user_id,
       'receiver_id' => $request->get('receiver_id'),
       'message' => $request->get('message')
    	]);
	$this->incrementDefaultTime();
	$this->dumpDefaultDB();
	$this->restoreBackupDB();
    }

    public function sendMessageDefaultOnly(Request $request){
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	$message = Message::create([
       'sender_id' => $user_id,
       'receiver_id' => $request->get('receiver_id'),
       'message' => $request->get('message')
    	]);
	$this->incrementDefaultTime();
	$this->dumpDefaultDB();
    }

    public function sendMessageBackupOnly(Request $request){
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	BackupMessage::create([
	'sender_id' => $user_id,
       'receiver_id' => $request->get('receiver_id'),
       'message' => $request->get('message')
    	]);
	$this->incrementBackupTime();
	$this->dumpBackupDB();
    }
}