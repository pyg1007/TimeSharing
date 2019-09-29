<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $id = $_POST['userid'];
    $Table = $_POST['tablename'];
    $Person = "person".$id;
    $tablename = "shareroom".$Table;

    $RoomMemeberdel  = mysqli_query($con, "DELETE FROM $tablename WHERE userid = '$id'");
    $Memberdel = mysqli_query($con, "DELETE FROM $Person WHERE tablename = '$tablename'");


    mysqli_close($con);
?>