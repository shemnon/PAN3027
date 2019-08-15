pragma solidity ^0.5.0;
contract SimpleStorage2 {

    bytes32 public storedData;
    bytes32 public storedData2;

    event Change(string message, bytes32 newVal);

    function set(bytes32 x, bytes32 y) public {
        emit Change("set", x);
        storedData = x;
        storedData2 = y;
    }

    function get() public view returns (bytes32 retVal, bytes32 retVal2) {
        return (storedData,storedData2);
    }

}