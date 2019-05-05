pragma solidity >=0.5.7 <0.6.0;

import "./PasswordsAccount.sol";

contract PasswordsBank {

    mapping (address=>PasswordsAccount) passwordAccounts;

    event PasswordAccountReady(address accountAddress);

    modifier accountOwnerOnly() {
        require(msg.sender == address(passwordAccounts[msg.sender].getOwner()), "You are unauthorized to access this account.");
        _;
    }

    modifier accountDoesntExist() {
        require(address(passwordAccounts[msg.sender]) == address(0), "Account Already Exists.");
        _;
    }

    function newPasswordAccount()
        public
        accountDoesntExist
    {
        passwordAccounts[msg.sender] = new PasswordsAccount(msg.sender);

        emit PasswordAccountReady(address(passwordAccounts[msg.sender]));
    }


    function addNewPassword(string memory resourceType, string memory resource, string memory login, string memory password)
        public
        accountOwnerOnly
    {
        passwordAccounts[msg.sender].addPassword(resourceType, resource, login, password);
    }


    function getPasswords()
        public
        view
        accountOwnerOnly
        returns (string memory)
    {
        return passwordAccounts[msg.sender].getAllPasswords();
    }

}

// "url", "mellowpixels.com", "varlon", "Sp2k68s15"
// "url", "google.com", "dmitry.ulyanov", "a;dfjhasf"
// "sftp", "sftp://mellow.com", "mellow", "Sp2k68s15"
// "ssh", "sftp://mellow.com", "coder", "Sp2k68s15"


