<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\Coordinate;
use JWTAuth;

class MapsController extends Controller
{
     public function getCoord()
    {
        return Coordinate::all();

    }

    public function setCoord(Request $request){
	$checksum = $request->get('checksum');
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
        $secret_key = $user_id.$token;
        $all_data = $request->except('checksum');
	$all_data_string = "";
	$all_data_string .= $request->get('latitude');
	$all_data_string .= $request->get('longitude');
	$all_data_string .= $request->get('type');
	$all_data_string .= $request->get('report_text');
	$hashValue = strtoupper(hash_hmac("sha256",$all_data_string,$secret_key));
	if(!($checksum===$hashValue)){
		return response()->json([
		'message'=>'Manipulerad/Korrupt data!'
		]);
	}
	$testing = $request->get('test');
	if($testing === 'true'){
	return response()->json([
	'message'=> 'Skulle ha satt in i DB, men testing mode on, avbryter...'
	]);
	}
    // grab coordinates from the request
        return Coordinate::create([
       'latitude' => $request->get('latitude'),
       'longitude' => $request->get('longitude'),
       'type' => $request->get('type'),
       'report_text' => $request->get('report_text')
    ]);
    }
    
    public function delCoords(){
    DB::table('coordinates')->delete();
    }
}
