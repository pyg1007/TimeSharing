<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $userid = $_POST['userid'];
    $uuid = $_POST['uuid'];

    $result = mysqli_query($con, "UPDATE USER SET uuid = '$uuid' WHERE userid = '$userid'");

    if ($result) {
        echo "success";
    }else{
        echo "Failure";
    }
    
    mysqli_close($con);
?>