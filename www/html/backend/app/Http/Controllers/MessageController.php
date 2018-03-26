<?php

namespace App\Http\Controllers;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\User;
use App\Message;

class MessageController extends Controller
{
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
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return Message::create([
       'sender_id' => $user_id,
       'receiver_id' => $request->get('receiver_id'),
       'message' => $request->get('message')
    	]);
    }
}