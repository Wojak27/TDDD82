<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\Coordinate;
use JWTAuth;

class MapsController extends Controller
{
	 public function getCoord() {
//Latitude: 1 deg = 110.574 km => 0.0904372 deg = 10 km => 0.0452186 deg = 5 km
//Longitude: 1 deg = 111.320*cos(latitude) km => 1/(11.132*cos(latitide)) deg = 10 km
	try {
		if (! $user = JWTAuth::parseToken()->authenticate()) {
			\Log::info('Unknown person tried to make access restricted maps pins, NOT approved.');
			return Coordinate::where('type', '=', 1)->get(); //return unrestricted pins
		}
		if($user->admin){
			\Log::info($user->name . ' is super-admin, showing all pins.');
			return Coordinate::all();
		}
		\Log::info($user->name . ' is NOT super-admin, showing type 1 and 2');
			return Coordinate::where('type','!=',3)->get();
	}catch(\Exception $e){
		\Log::info('Unknown person, showing unrestricted pins.');
		return Coordinate::where('type','=',1)->get();
	}


	// the token is valid and we have found the user via the sub claim
   }
    
    public function getNearestCoord(Request $request) {
    $latitude = floatval($request->get('latitude'));
    $longitude = floatval($request->get('longitude'));
    $latitudetop = $latitude + 0.0452186;
    $latitudebuttom = $latitude - 0.0452186;
    $longitudediff = 1/(2*11.132*cos($latitude));
    if($longitudediff < 0){
	$longitudediff = (-1)*$longitudediff;
    }
    $longitudetop = $longitude + $longitudediff;
    $longitudebuttom = $longitude - $longitudediff;
    
    //Latitude: 1 deg = 110.574 km => 0.0904372 deg = 10 km => 0.0452186 deg = 5 km
    //Longitude: 1 deg = 111.320*cos(latitude) km => 1/(2*11.132*cos(latitide)) deg = 5 km
	try {
		if (! $user = JWTAuth::parseToken()->authenticate()) {
			
			return Coordinate::whereRaw('type = 1 and (latitude < ? and latitude > ?) and (longitude > ? and longitude < ?)', [$latitudetop, $latitudebuttom, $longitudebuttom, $longitudetop])->get(); //return unrestricted pins
		}
		if($user->admin){
			
			return Coordinate::all();
		}
		
			return Coordinate::whereRaw('type != 3 and (latitude < ? and latitude > ?) and (longitude > ? and longitude < ?)', [$latitudetop, $latitudebuttom, $longitudebuttom, $longitudetop])->get();
	}catch(\Exception $e){
		
		return Coordinate::whereRaw('type = 1 and (latitude < ? and latitude > ?) and (longitude > ? and longitude < ?)', [$latitudetop, $latitudebuttom, $longitudebuttom, $longitudetop])->get(); //return unrestricted pins
	}


	// the token is valid and we have found the user via the sub claim
   }
    

    public function setCoord(Request $request){
	$checksum = $request->get('checksum');
	$token = JWTAuth::getToken();
	$user_id = strtoupper(JWTAuth::toUser($token)->id);
        $secret_key = $user_id.$token;
	$all_data_string = "";
	$all_data_string .= $request->get('latitude');
	$all_data_string .= $request->get('longitude');
	$all_data_string .= $request->get('type');
	$all_data_string .= $request->get('report_text');
	$hashValue = strtoupper(hash_hmac("sha256",$all_data_string,$secret_key));
	if(!($checksum===$hashValue)){
		\Log::error('Server 1:Maps:  Corrupt/manipulated data, not put in DB');
		return response()->json([
		'message'=>'Server 1: Maps: Corrput/Manipulated data, not put in DB'
		]);
	}
	$testing = $request->get('test');
	if($testing === 'true'){
	\Log::info('Server 1: Testing mode on, not put in DB');
	return response()->json([
	'message'=> 'Server 1: Testing mode on, not put in DB'
	]);
	}
    // grab coordinates from the request
	\Log::info('Server 1: Coordinate was put in DB');
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
