<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $userid = $_POST['userid'];
    $userpw = $_POST['userpassword'];
    $userintroduce = $_POST['userintroduce'];
    $uuid = $_POST['uuid'];
    $username = $_POST['username'];
    $id = "person".$userid;

    $result  = mysqli_query($con, "INSERT INTO USER ( userid, userpassword , username, userintroduce, uuid) VALUES ('$userid', '$userpw','$username', '$userintroduce', '$uuid')");

    $tablecreate = mysqli_query($con, "CREATE TABLE $id (tablename VARCHAR(50) NOT NULL, PRIMARY KEY(tablename))");

    if($result){
        echo "회원 가입에 성공하셨습니다.";
    }else{
        echo "회원가입실패";
    }

    mysqli_close($con);
?>