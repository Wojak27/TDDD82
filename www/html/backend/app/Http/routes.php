<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

Route::get('/', function () {
    return view('welcome');
});
Route::group(['prefix' => '', 'middleware' => 'auth:api'], function(){
    Route::get('/users', function () {
        return 'Tjosan';
    });
});

Route::get('/map', funciton () {
	return 'testet';
});

Route::get('/database', 'DatabaseController@show');
