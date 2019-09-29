<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $id = $_POST['userid'];
    $pw = $_POST['userpassword'];
    $introduce = $_POST['userintroduce'];


    $result  = mysqli_query($con, "UPDATE USER SET userpassword = '$pw', userintroduce = '$introduce' WHERE userid = '$id'");

    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>