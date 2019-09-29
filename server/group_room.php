<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $usertn = $_POST['tablename'];
    $tablename = "shareroom".$usertn;

    $tablecreate = mysqli_query($con, "CREATE TABLE $tablename (userid VARCHAR(50) NOT NULL, tableexplanation VARCHAR(250))");

    if($tablecreate){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>