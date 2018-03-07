<?php

namespace App;
use Illuminate\Database\Eloquent\Model;
class Coordinate extends Model
{
    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $primaryKey = 'id';
    
    protected $fillable = [
        'latitude', 'longitude', 'type', 'report_text'
    ];
    public $incrementing = true;
    protected $keyType = 'string';
    public $timestamps = false;

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */

}
