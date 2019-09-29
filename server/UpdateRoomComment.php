<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $tablename = "shareroom".$table;
    $Tableexplanation = $_POST['tableexplanation'];

    $member = mysqli_query($con, "UPDATE $tablename SET tableexplanation = '$Tableexplanation'");

    $sql = "UPDATE $tablename SET tableexplanation = '$Tableexplanation'";

    echo $sql;

    mysqli_close($con);
?>