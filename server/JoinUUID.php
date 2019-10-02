<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $tablename = $_POST['tablename'];
    $sharetablename = "shareroom".$tablename;

    $result  = mysqli_query($con, "SELECT uuid FROM USER JOIN $sharetablename ON USER.userid = $sharetablename.userid");

    $data = array();

    if($result){
        while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('uuid'=>$row[0]
        ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
    }else{
        echo "failure";
    }
    mysqli_close($con);
?>