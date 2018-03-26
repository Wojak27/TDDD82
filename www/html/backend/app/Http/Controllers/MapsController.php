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
	if(JWTAuth::getToken()){
	return Coordinate::all();
	}
	else{
	return Coordinate::where('type', '=', 1)->get();
	}
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
		\Log::error('Korrupt/manipulerad data, inget sattes in i DB');
		return response()->json([
		'message'=>'Manipulerad/Korrupt data!'
		]);
	}
	$testing = $request->get('test');
	if($testing === 'true'){
	\Log::info('Skulle ha satt in i DB, men testing mode is on');
	return response()->json([
	'message'=> 'Skulle ha satt in i DB, men testing mode on, avbryter...'
	]);
	}
    // grab coordinates from the request
	\Log::info('Koordinat lades till');
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
