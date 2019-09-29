<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $Tableshare = "shareroom".$table;

    $result  = mysqli_query($con, "DROP TABLE IF EXISTS $Tableshare");

    $otherdel = mysqli_query($con, "DELETE FROM GroupRoomMenu WHERE RoomId = '$Tableshare'");
    
    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>