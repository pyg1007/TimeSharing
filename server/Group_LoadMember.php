<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $tablename = "shareroom".$table;

   
    $result  = mysqli_query($con, "SELECT userid FROM $tablename");

    $data = array();
    if($result){
        while($row=mysqli_fetch_array($result)){
            array_push($data, 
                array('id'=>$row[0],
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