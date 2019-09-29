<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $usertn = $_POST['tablename'];
    $tablename = "shareroom".$usertn;

    $member = mysqli_query($con, "SELECT userid FROM $tablename");

    $data = array();

    if($member){
        while($row=mysqli_fetch_array($member)){
            array_push($data, 
                array('userid'=>$row[0],
                'tableexplanation'=>$row[1]
            ));
        }
        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

    mysqli_close($con);
?>