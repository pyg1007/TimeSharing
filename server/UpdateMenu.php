<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $Menuname = $_POST['menuname'];
    $changeMenuname = $_POST['changemenuname'];
    $starttime = $_POST['start'];
    $endtime = $_POST['end'];
    $start = $_POST['starttime'];
    $end = $_POST['endtime'];
    $tablename = "shareroom".$table;

    $result  = mysqli_query($con, "UPDATE GroupRoomMenu SET RoomId = '$tablename', Menuname = '$changeMenuname', Starttime = '$start', Endtime = '$end' WHERE RoomId = '$tablename' AND Menuname = '$Menuname' AND Starttime = '$starttime' AND Endtime = '$endtime'");

    if($result){
        echo 'success';
    }else{
        echo 'failure';
    }

    mysqli_close($con);
?>