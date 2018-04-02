<?php
namespace App;
use Illuminate\Database\Eloquent\Model;
class BackupMessage extends Model
{
    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $connection = 'mysql_backup';
    protected $table = 'messages';
    protected $primaryKey = 'message_id';
    
    protected $fillable = [
        'sender_id', 'receiver_id', 'message', 'has_received'
    ];
    public $incrementing = true;
    protected $keyType = 'string';
    public $timestamps = false;

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */

	public function user(){
	 return $this->belongsTo('App\User','sender_id');
	}

}
