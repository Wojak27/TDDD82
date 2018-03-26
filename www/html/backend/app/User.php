<?php

namespace App;

use Illuminate\Foundation\Auth\User as Authenticatable;

class User extends Authenticatable
{
    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'id', 'password'
    ];
    public $incrementing = false;
    protected $keyType = 'string';
    public $timestamps = false;

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */
    protected $hidden = [
        'password','api_token'
    ];

     public function sentMessages(){
	return $this->hasMany('App\Message','sender_id');
     }
     public function receivedMessages(){
	return $this->hasMany('App\Message','receiver_id');
     }
}
