<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $userid = $_POST['userid'];
    $table = $_POST['tablename'];
    $tablename = "shareroom".$table;

   
    $result  = mysqli_query($con, "INSERT INTO $tablename (userid) VALUES ('$userid')");

    if($result){
        echo 'success';
    }else{
        echo "failure";
    }   

    mysqli_close($con);
?>