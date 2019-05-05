package com.mellowpixels;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class PasswordsAccount extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50604051602080610a0a8339810180604052602081101561003057600080fd5b5051600080546001600160a01b039092166001600160a01b03199092169190911790556109a8806100626000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80631765147e14610046578063893d20e81461027b578063b2a43c8c1461029f575b600080fd5b6102796004803603608081101561005c57600080fd5b810190602081018135600160201b81111561007657600080fd5b82018360208201111561008857600080fd5b803590602001918460018302840111600160201b831117156100a957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156100fb57600080fd5b82018360208201111561010d57600080fd5b803590602001918460018302840111600160201b8311171561012e57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561018057600080fd5b82018360208201111561019257600080fd5b803590602001918460018302840111600160201b831117156101b357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561020557600080fd5b82018360208201111561021757600080fd5b803590602001918460018302840111600160201b8311171561023857600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061031c945050505050565b005b6102836103e4565b604080516001600160a01b039092168252519081900360200190f35b6102a76103f4565b6040805160208082528351818301528351919283929083019185019080838360005b838110156102e15781810151838201526020016102c9565b50505050905090810190601f16801561030e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b604080516080810182528581526020808201869052918101849052606081018390526001805480820180835560009290925282518051929460049092027fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf6019261038992849201906108e4565b5060208281015180516103a292600185019201906108e4565b50604082015180516103be9160028401916020909101906108e4565b50606082015180516103da9160038401916020909101906108e4565b5050505050505050565b6000546001600160a01b03165b90565b60408051808201825260018152600160f81b605b026020808301919091528251908101909252600080835260609283915b600154811015610865576001818154811061043c57fe5b90600052602060002090600402016000016001828154811061045a57fe5b90600052602060002090600402016001016001838154811061047857fe5b90600052602060002090600402016002016001848154811061049657fe5b90600052602060002090600402016003016040516020018080600160f81b607b02815250600101807f227265736f7572636554797065223a22000000000000000000000000000000008152506010018580546001816001161561010002031660029004801561053c5780601f1061051a57610100808354040283529182019161053c565b820191906000526020600020905b815481529060010190602001808311610528575b505080600160f21b61088b02815250600201807f227265736f75726365223a220000000000000000000000000000000000000000815250600c01848054600181600116156101000203166002900480156105cd5780601f106105ab5761010080835404028352918201916105cd565b820191906000526020600020905b8154815290600101906020018083116105b9575b505080600160f21b61088b0281525060020180600160b91b68113637b3b4b7111d11028152506009018380546001816001161561010002031660029004801561064d5780601f1061062b57610100808354040283529182019161064d565b820191906000526020600020905b815481529060010190602001808311610639575b505080600160f21b61088b02815250600201807f2270617373776f7264223a220000000000000000000000000000000000000000815250600c01828054600181600116156101000203166002900480156106de5780601f106106bc5761010080835404028352918201916106de565b820191906000526020600020905b8154815290600101906020018083116106ca575b505080600160f91b60110281525060010180600160f81b607d028152506001019450505050506040516020818303038152906040529250600180805490500381101561074857604051806040016040528060018152602001600160fa1b600b02815250915061075b565b6040518060200160405280600081525091505b8383836040516020018084805190602001908083835b602083106107905780518252601f199092019160209182019101610771565b51815160209384036101000a600019018019909216911617905286519190930192860191508083835b602083106107d85780518252601f1990920191602091820191016107b9565b51815160209384036101000a600019018019909216911617905285519190930192850191508083835b602083106108205780518252601f199092019160209182019101610801565b6001836020036101000a038019825116818451168082178552505050505050905001935050505060405160208183030381529060405293508080600101915050610425565b50826040516020018082805190602001908083835b602083106108995780518252601f19909201916020918201910161087a565b6001836020036101000a03801982511681845116808217855250505050505090500180600160f81b605d02815250600101915050604051602081830303815290604052935050505090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061092557805160ff1916838001178555610952565b82800160010185558215610952579182015b82811115610952578251825591602001919060010190610937565b5061095e929150610962565b5090565b6103f191905b8082111561095e576000815560010161096856fea165627a7a72305820682f8ac5d56cf27e7fe8c8826fafa93d0321fac157ae33896bdb82762be55ed60029";

    public static final String FUNC_ADDPASSWORD = "addPassword";

    public static final String FUNC_GETOWNER = "getOwner";

    public static final String FUNC_GETALLPASSWORDS = "getAllPasswords";

    @Deprecated
    protected PasswordsAccount(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PasswordsAccount(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PasswordsAccount(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PasswordsAccount(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> addPassword(String resourceType, String resource, String login, String password) {
        final Function function = new Function(
                FUNC_ADDPASSWORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(resourceType), 
                new org.web3j.abi.datatypes.Utf8String(resource), 
                new org.web3j.abi.datatypes.Utf8String(login), 
                new org.web3j.abi.datatypes.Utf8String(password)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getOwner() {
        final Function function = new Function(FUNC_GETOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getAllPasswords() {
        final Function function = new Function(FUNC_GETALLPASSWORDS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<PasswordsAccount> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String accountOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountOwner)));
        return deployRemoteCall(PasswordsAccount.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<PasswordsAccount> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String accountOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountOwner)));
        return deployRemoteCall(PasswordsAccount.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PasswordsAccount> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String accountOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountOwner)));
        return deployRemoteCall(PasswordsAccount.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PasswordsAccount> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String accountOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountOwner)));
        return deployRemoteCall(PasswordsAccount.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static PasswordsAccount load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PasswordsAccount(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PasswordsAccount load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PasswordsAccount(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PasswordsAccount load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PasswordsAccount(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PasswordsAccount load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PasswordsAccount(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
