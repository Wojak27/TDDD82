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
	\Log::info('Server 1: All sent messages returned to client');
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return User::find($user_id)->sentMessages;        
    }

     public function getAllReceivedMessages(Request $request){
	\Log::info('Server 1: All reveived messages returned to client');
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return User::find($user_id)->receivedMessages;        
    }

     public function getChatMessages(Request $request){
	\Log::info('Server 1: All messages returned to client');
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
	$checksum = $request->get('checksum');
	$this->restoreDatabaseConstiency();
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	$secret_key = $user_id.$token;
	$all_data_string = "";
	$all_data_string .= $request->get('receiver_id');
	$all_data_string .= $request->get('message');
	$hashValue = strtoupper(hash_hmac("sha256", $all_data_string,$secret_key));
	if(!($checksum==$hashValue)){
		\Log::info('Message: Corrupt/Manipulated data, not put in DB');
		return response()->json([
		'message'=>'Server1: Message: Corrupt/Manipulated data, not put in DB', $hashValue, $checksum
		]);
	}
	\Log::info('Server 1: Message added to DB and backup');
	$message = Message::create(['sender_id' => $user_id,
       'receiver_id' => $request->get('receiver_id'),
       'message' => $request->get('message')
    	]);
	$this->incrementDefaultTime();
	$this->dumpDefaultDB();
	$this->restoreBackupDB();
	return $message;
    }

    public function sendMessageDefaultOnly(Request $request){
	\Log::info('Server 1: Message sent and added to DB');
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
	\Log::info('Server 1: says Message sent and added to Backup');
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
