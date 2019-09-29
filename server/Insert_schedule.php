<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $userid = $_POST['userid'];
    $title = $_POST['title'];
    $contents = $_POST['contents'];
    $previoustime = $_POST['previoustime'];
    $aftertime = $_POST['aftertime'];
    $savedate = $_POST['savedate'];


    $result  = mysqli_query($con, "INSERT INTO USER_CONTENTS (userid, title, contents, previoustime, aftertime, savedate) VALUES ('$userid', '$title', '$contents', '$previoustime', '$aftertime','$savedate')");

    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>