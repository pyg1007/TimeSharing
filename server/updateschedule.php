<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $_id = $_POST['userid'];
    $title = $_POST['title'];
    $contents = $_POST['contents'];
    $previoustime = $_POST['previoustime'];
    $aftertime = $_POST['aftertime'];
    $savedate = $_POST['savedate'];


    $result  = mysqli_query($con, "UPDATE USER_CONTENTS SET title = '$title', contents = '$contents', previoustime = '$previoustime', aftertime = '$aftertime', savedate = '$savedate' WHERE _id = '$_id'");

    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>