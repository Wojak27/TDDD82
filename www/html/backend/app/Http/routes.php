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
    Route::get('/secret', 'DatabaseController@secret');
    Route::post('/logout', 'AuthenticateController@logout');
});

Route::post('/login', 'AuthenticateController@authenticate');
Route::post('/register', 'AuthenticateController@register');
Route::get('/database', 'DatabaseController@show');
