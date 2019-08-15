pragma solidity ^0.5.0;
import './SimpleStorage2.sol';
contract SimpleStorage {

    bytes32 public storedData;
    bytes32 public storedData2;
    address public ss2addr;

    event Change(string message, bytes32 newVal);

    function set(bytes32 x, bytes32 y) public {
        emit Change("set", x);
        storedData = x;
        storedData2 = y;
    }

    function setContract() public {
        SimpleStorage2 sscontract = new SimpleStorage2();
        ss2addr = address(sscontract);
    }

    function setValues(bytes32 x,  bytes32 y) public {
        SimpleStorage2 instance = SimpleStorage2(ss2addr);

        instance.set(x, y);
    }

    function getLocal() public view returns (bytes32 retVal, bytes32 retVal2) {
        return (storedData,storedData2);
    }

    function getInstance() public view returns (address retVal3,bytes32 retVal, bytes32 retVal2) {
        SimpleStorage2 instance = SimpleStorage2(ss2addr);
        return (ss2addr, instance.storedData(), instance.storedData2());
    }

    function getAddress() public view returns (address retVal){
        return ss2addr;
    }

}