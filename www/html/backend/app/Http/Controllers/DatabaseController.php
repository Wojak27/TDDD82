<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use DB;

class DatabaseController extends Controller
{
    public function show()
    {
    	return DB::table('user')->get();
    }
}