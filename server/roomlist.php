<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $id = $_POST['userid'];
    $Person = "person".$id;

    $result  = mysqli_query($con, "SELECT * FROM $Person");

    $data = array();   
    if($result){      
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array(      
            'room'=>$row[0]
        ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
}  

    if($result){
        echo 'success';
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>