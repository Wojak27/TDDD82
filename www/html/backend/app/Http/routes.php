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
Route::group(['prefix' => '', 'middleware' => 'jwt.auth'], function(){
    Route::get('/users', function () {
        return 'Tjosan';
    });
});

Route::post('/login', 'AuthenticateController@authenticate');
Route::get('/login', 'AuthenticateController@show');

Route::get('/database', 'DatabaseController@show');
