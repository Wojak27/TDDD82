<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use DB;

class DatabaseController extends Controller
{
    public function show()
    {
    	return DB::table('users')->get();
    }

     public function secret()
    {
    	return response()->json(['message' => 'TJOSAN']);
    }

}