pragma solidity >=0.5.7 <0.6.0;

contract PasswordsAccount {

    struct PasswordRecord {
        string resourceType;
        string resource;
        string login;
        string password;
    }

    address private owner;
    PasswordRecord[] private passwords;

    constructor(address accountOwner) public
    {
        owner = accountOwner;
    }


    modifier accountOwnerOnly () {
        require(msg.sender == owner, "Sender not authorized.");
        _;
    }



    function addPassword(string memory resourceType, string memory resource, string memory login, string memory password)
        public
        // accountOwnerOnly
    {
        passwords.push(PasswordRecord(resourceType, resource, login, password));
    }



    function getAllPasswords()
        public
        view
        // accountOwnerOnly
        returns(string memory)
    {
        string memory output = "[";
        string memory rec;
        string memory hasComma = "";

        for(uint i = 0; i < passwords.length; i++ ){
             rec = string(abi.encodePacked(
                '{',
                '"resourceType":"', passwords[i].resourceType, '",',
                '"resource":"', passwords[i].resource, '",',
                '"login":"', passwords[i].login, '",',
                '"password":"', passwords[i].password, '"',
                "}"
            ));

            if(i < passwords.length - 1) {
                hasComma = ",";
            } else {
                hasComma = "";
            }

            output = string(abi.encodePacked(output, rec, hasComma));
        }

        return string(abi.encodePacked(output, "]"));
    }


    function getOwner()
        public
        view
        returns(address)
    {
        return owner;
    }
}