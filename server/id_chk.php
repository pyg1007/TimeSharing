<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $id = $_POST['userid'];

    $result = mysqli_query($con, "SELECT * FROM USER WHERE userid = '$id'");

    $row = mysqli_num_rows($result);

    if($row>0){
        echo "failure";
    }else{
        echo "success";
    }

    mysqli_close($con);
?>