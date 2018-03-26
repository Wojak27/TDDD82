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
    Route::get('/secret', 'UserController@secret');
    Route::post('/logout', 'AuthenticateController@logout');
    Route::post('/setCoord', 'MapsController@setCoord');
    Route::post('/delCoords', 'MapsController@delCoords');
    Route::get('/sentMessages', 'MessageController@getAllSentMessages');
    Route::get('/receivedMessages','MessageController@getAllReceivedMessages');
    Route::get('/chatMessages','MessageController@getChatMessages');
    Route::post('/sendMessage','MessageController@sendMessage');
    Route::get('/users', 'UserController@users');
});

Route::post('/login', 'AuthenticateController@authenticate');
Route::post('/register', 'AuthenticateController@register');
Route::get('/coord', 'MapsController@getCoord');

/*
|--------------------------------------------------------------------------
| Observera!
|--------------------------------------------------------------------------
|
| Nedan här har Robin & Tobbe lite saker för sin kandidat pga Niklas kan ej
| ge oss en server, men vafan bear with us. ÄNDRA INGET HÄR NEDANFÖR!!!!
|
*/
Route::group(['prefix' => 'ct'], function(){
    Route::get('/secret', 'UserController@secret');
});