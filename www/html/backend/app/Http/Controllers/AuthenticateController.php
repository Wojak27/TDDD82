<?php

namespace App\Http\Controllers;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\User;

class AuthenticateController extends Controller
{
    public function authenticate(Request $request)
    {
        // grab credentials from the request
        $credentials = $request->only('id', 'password');

        try {
            // attempt to verify the credentials and create a token for the user
            if (! $token = JWTAuth::attempt($credentials)) {
                return response()->json(['USER_AUTH_STATUS' => 'USER_NOT_AUTHENTICATED'], 401);
            }
        } catch (JWTException $e) {
            // something went wrong whilst attempting to encode the token
            return response()->json(['USER_AUTH_STATUS' => 'USER_NOT_AUTHENTICATED'], 500);
        }

        // all good so return the token
	$USER_AUTH_STATUS = "USER_AUTHENTICATED";
        return response()->json(compact('token', 'USER_AUTH_STATUS'));
    }

    public function show()
    {
        return DB::table('users')->get();
    }

    public function logout(Request $request){
      $token = $request->get('token');
      JWTAuth::invalidate($token);
      return response()->json(['status'=>true, 'message'=>'invalidated token']);
    }

     public function register(Request $request)
     {
        $user = User::create([
           'id' => $request->get('id'),
           'password' => bcrypt($request->get('password')) 
        ]);
        return response()->json(['status'=>true, 'message' => 'created user']);
    }
}