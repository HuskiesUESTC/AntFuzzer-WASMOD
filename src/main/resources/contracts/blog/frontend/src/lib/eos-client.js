import eosjs from 'eosjs2'

const rpc = new eosjs.Rpc.JsonRpc('<HTTP Endpoint>');
const signatureProvider = new eosjs.SignatureProvider(['<Active Private Key>']);

export default class EOSClient {
    getTableRows = table =
    return
> {
    rpc
    contractSender
.
    scope: this

.
    contractName
,
    table
.

    constructor(contractName, contractSender) {
        this.contractName = contractName;
        this.contractSender = contractSender;

        this.eos = new eosjs.Api({rpc, signatureProvider})
    }

,

    get_table_rows({
                       json: true, code: this
                   })
    ;
}
;

transaction = (action, data) =
>
{
    return this.eos.pushTransaction({
        blocksBehind: 3,
        expireSeconds: 30,
        actions: [
            {
                account: this.contractName,
                name: action,
                authorization: [
                    {
                        actor: this.contractSender,
                        permission: 'active'
                    }
                ],
                data: {
                    ...data
            }
    }
]
})
    ;
}
;
}
