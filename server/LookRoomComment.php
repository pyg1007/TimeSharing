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

    $tableexplanation = mysqli_query($con, "SELECT tableexplanation FROM $tablename WHERE userid = '$userid'");

    $data = array();
    
    if($tableexplanation){
        while($row=mysqli_fetch_array($tableexplanation)){
            array_push($data, 
                array('tableexplanation'=>$row[0]
            ));
        }
        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    mysqli_close($con);
?>