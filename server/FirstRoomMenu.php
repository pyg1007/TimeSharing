<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $tablename = "shareroom".$table;

    $check = mysqli_query($con, "SELECT * FROM GroupRoomMenu WHERE RoomId = '$tablename' AND MenuName = '일반'");

    $row = mysqli_num_rows($check);

    if($row==0){
        $basic = mysqli_query($con, "INSERT INTO GroupRoomMenu(RoomId, MenuName, Starttime, Endtime) VALUES ('$tablename', '일반', 0, 24)");
    }

    
    mysqli_close($con);
?>