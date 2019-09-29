<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $userid = $_POST['userid'];
    $usertn = $_POST['tablename'];
    $id = "person".$userid;
    $tablename = "shareroom".$usertn;

    $insert = mysqli_query($con, "INSERT INTO $id VALUES ('$tablename')");
    
    mysqli_close($con);
?>