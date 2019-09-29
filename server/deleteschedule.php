<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $index = $_POST['index'];
    $savedate = $_POST['savedate'];
    $id = $_POST['userid'];
    $title = $_POST['title'];

    $result  = mysqli_query($con, "DELETE FROM USER_CONTENTS WHERE userid = '$id' AND title = '$title' AND savedate = '$savedate' AND _id = '$index'");

    $sql = "DELETE FROM USER_CONTENTS WHERE userid = '$id' AND title = '$title' AND savedate = '$savedate' AND _id = '$index' ";

    echo $sql;
    
    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>