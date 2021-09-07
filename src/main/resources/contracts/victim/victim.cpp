#include <eosio/eosio.hpp>
#include <eosio/contract.hpp>
#include <eosio/transaction.hpp>
#include <eosio/serialize.hpp>
#include <eosio/asset.hpp>
#include <eosio/time.hpp>

using namespace eosio;

class [[eosio::contract]] victim : public contract {
  public:
    using contract::contract;
    victim(name receiver, name code, datastream<const char *> ds)
        : contract(receiver, code, ds) {};

    struct [[eosio::table]] balance {
        name user;
        asset funds;
        uint64_t primary_key() const { return user.value; }  
        
    };
    using balance_table = eosio::multi_index<name("balance"),balance>;

    // [[eosio::on_notify("eosio.token::transfer")]]
    void deposit(name user, name to, asset quantity, std::string memo) {
        if (user == get_self()) {
            return;
        }
        balance_table balance(get_self(), get_self().value);
        auto it = balance.find(user.value);

        if (it != balance.end()) {
            balance.modify(it, get_self(), [&](auto &row) { row.funds += quantity; });
        } else {
            balance.emplace(get_self(), [&](auto &row) {
                row.user = user;
                row.funds = quantity;
            });
        }
    }

    [[eosio::action]]
    void vulnerable(name user) {
        // require_auth(user);
        balance_table balance(get_self(), get_self().value);
        auto it = balance.find(user.value);
        check(it != balance.end(), "No money.");
        balance.modify(it, get_self(), [&](auto &row) { row.funds = row.funds; });
        print("no check");
    }

    // [[eosio::action]]
    // void withdraw(name user) {
    //     require_auth(user);

    //     balance_table balance(get_self(), get_self().value);
    //     auto it = balance.find(user.value);
    //     check(it != balance.end(), "No money.");

    //     action{
    //         permission_level{get_self(), name("active")},
    //         name("eosio.token"),
    //         name("transfer"),
    //         std::make_tuple(_self, user, it->funds, std::string("with draw."))
    //     }.send();
    //     balance.erase(it);
        
    // }

  private:
};

#define EOSIO_DISPATCH_VICTIM(TYPE, MEMBERS) \
extern "C" { \
    void apply(uint64_t receiver, uint64_t code, uint64_t action) { \
        if (code == receiver) { \
            switch (action) { \
                EOSIO_DISPATCH_HELPER(TYPE, MEMBERS) \
            } \
        } else if (code == name("eosio.token").value && action == name("transfer").value) { \
            execute_action(name(receiver), name(code), &victim::deposit); \
        } \
    } \
} \

EOSIO_DISPATCH_VICTIM(victim, (deposit)(vulnerable))