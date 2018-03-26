<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use DB;
use JWTAuth;
use App\User;

class UserController extends Controller
{
    public function users()
    {
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
	return User::where('id','!=',$user_id)->get();
    }

     public function secret()
    {
    	return response()->json(['message' => 'TJOSAN']);
    }

}