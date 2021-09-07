(module
  (type $t0 (func))
  (type $t1 (func (param i32 i64)))
  (type $t2 (func (param i32 i64 i64 i32 i32)))
  (type $t3 (func (param i32 i32)))
  (type $t4 (func (param i32 i32 i32) (result i32)))
  (type $t5 (func (result i32)))
  (type $t6 (func (param i32 i32) (result i32)))
  (type $t7 (func (param i64 i64 i64 i64) (result i32)))
  (type $t8 (func (param i64)))
  (type $t9 (func (param i32 f32)))
  (type $t10 (func (param i32 i64 i64 i64 i64)))
  (type $t11 (func (param i32 f64)))
  (type $t12 (func (param i64 i64) (result f64)))
  (type $t13 (func (param i64 i64) (result f32)))
  (type $t14 (func (param i64 i64) (result i32)))
  (type $t15 (func (param i32)))
  (type $t16 (func (result i64)))
  (type $t17 (func (param i64 i64 i64 i64 i32 i32) (result i32)))
  (type $t18 (func (param i32 i64 i32 i32)))
  (type $t19 (func (param i32) (result i32)))
  (type $t20 (func (param i64 i64 i64)))
  (type $t21 (func (param i64 i64 i32) (result i32)))
  (type $t22 (func (param i32 i32 i64 i32)))
  (type $t23 (func (param i64 i64)))
  (type $t24 (func (param i32 i32 i32 i32)))
  (import "env" "eosio_assert" (func $env.eosio_assert (type $t3)))
  (import "env" "memset" (func $env.memset (type $t4)))
  (import "env" "action_data_size" (func $env.action_data_size (type $t5)))
  (import "env" "read_action_data" (func $env.read_action_data (type $t6)))
  (import "env" "abort" (func $env.abort (type $t0)))
  (import "env" "db_find_i64" (func $env.db_find_i64 (type $t7)))
  (import "env" "memcpy" (func $env.memcpy (type $t4)))
  (import "env" "require_auth" (func $env.require_auth (type $t8)))
  (import "env" "send_inline" (func $env.send_inline (type $t3)))
  (import "env" "memmove" (func $env.memmove (type $t4)))
  (import "env" "__extendsftf2" (func $env.__extendsftf2 (type $t9)))
  (import "env" "__floatsitf" (func $env.__floatsitf (type $t3)))
  (import "env" "__multf3" (func $env.__multf3 (type $t10)))
  (import "env" "__floatunsitf" (func $env.__floatunsitf (type $t3)))
  (import "env" "__divtf3" (func $env.__divtf3 (type $t10)))
  (import "env" "__addtf3" (func $env.__addtf3 (type $t10)))
  (import "env" "__extenddftf2" (func $env.__extenddftf2 (type $t11)))
  (import "env" "__eqtf2" (func $env.__eqtf2 (type $t7)))
  (import "env" "__letf2" (func $env.__letf2 (type $t7)))
  (import "env" "__netf2" (func $env.__netf2 (type $t7)))
  (import "env" "__subtf3" (func $env.__subtf3 (type $t10)))
  (import "env" "__trunctfdf2" (func $env.__trunctfdf2 (type $t12)))
  (import "env" "__getf2" (func $env.__getf2 (type $t7)))
  (import "env" "__trunctfsf2" (func $env.__trunctfsf2 (type $t13)))
  (import "env" "prints_l" (func $env.prints_l (type $t3)))
  (import "env" "__unordtf2" (func $env.__unordtf2 (type $t7)))
  (import "env" "__fixunstfsi" (func $env.__fixunstfsi (type $t14)))
  (import "env" "__fixtfsi" (func $env.__fixtfsi (type $t14)))
  (import "env" "db_next_i64" (func $env.db_next_i64 (type $t6)))
  (import "env" "prints" (func $env.prints (type $t15)))
  (import "env" "eosio_assert_code" (func $env.eosio_assert_code (type $t1)))
  (import "env" "current_receiver" (func $env.current_receiver (type $t16)))
  (import "env" "db_store_i64" (func $env.db_store_i64 (type $t17)))
  (import "env" "db_get_i64" (func $env.db_get_i64 (type $t4)))
  (import "env" "db_update_i64" (func $env.db_update_i64 (type $t18)))
  (import "env" "db_remove_i64" (func $env.db_remove_i64 (type $t15)))
  (func $f36 (type $t0)
    call $f40)
  (func $f37 (type $t19) (param $p0 i32) (result i32)
    (local $l1 i32) (local $l2 i32) (local $l3 i32)
    local.get $p0
    local.set $l1
    block $B0
      block $B1
        block $B2
          local.get $p0
          i32.const 3
          i32.and
          i32.eqz
          br_if $B2
          local.get $p0
          i32.load8_u
          i32.eqz
          br_if $B1
          local.get $p0
          i32.const 1
          i32.add
          local.set $l1
          loop $L3
            local.get $l1
            i32.const 3
            i32.and
            i32.eqz
            br_if $B2
            local.get $l1
            i32.load8_u
            local.set $l2
            local.get $l1
            i32.const 1
            i32.add
            local.tee $l3
            local.set $l1
            local.get $l2
            br_if $L3
          end
          local.get $l3
          i32.const -1
          i32.add
          local.get $p0
          i32.sub
          return
        end
        local.get $l1
        i32.const -4
        i32.add
        local.set $l1
        loop $L4
          local.get $l1
          i32.const 4
          i32.add
          local.tee $l1
          i32.load
          local.tee $l2
          i32.const -1
          i32.xor
          local.get $l2
          i32.const -16843009
          i32.add
          i32.and
          i32.const -2139062144
          i32.and
          i32.eqz
          br_if $L4
        end
        local.get $l2
        i32.const 255
        i32.and
        i32.eqz
        br_if $B0
        loop $L5
          local.get $l1
          i32.load8_u offset=1
          local.set $l2
          local.get $l1
          i32.const 1
          i32.add
          local.tee $l3
          local.set $l1
          local.get $l2
          br_if $L5
        end
        local.get $l3
        local.get $p0
        i32.sub
        return
      end
      local.get $p0
      local.get $p0
      i32.sub
      return
    end
    local.get $l1
    local.get $p0
    i32.sub)
  (func $f38 (type $t19) (param $p0 i32) (result i32)
    (local $l1 i32) (local $l2 i32) (local $l3 i32)
    block $B0
      block $B1
        block $B2
          block $B3
            local.get $p0
            i32.eqz
            br_if $B3
            i32.const 0
            i32.const 0
            i32.load offset=8204
            local.get $p0
            i32.const 16
            i32.shr_u
            local.tee $l1
            i32.add
            local.tee $l2
            i32.store offset=8204
            i32.const 0
            i32.const 0
            i32.load offset=8196
            local.tee $l3
            local.get $p0
            i32.add
            i32.const 7
            i32.add
            i32.const -8
            i32.and
            local.tee $p0
            i32.store offset=8196
            local.get $l2
            i32.const 16
            i32.shl
            local.get $p0
            i32.le_u
            br_if $B2
            local.get $l1
            memory.grow
            i32.const -1
            i32.eq
            br_if $B1
            br $B0
          end
          i32.const 0
          return
        end
        i32.const 0
        local.get $l2
        i32.const 1
        i32.add
        i32.store offset=8204
        local.get $l1
        i32.const 1
        i32.add
        memory.grow
        i32.const -1
        i32.ne
        br_if $B0
      end
      i32.const 0
      i32.const 8220
      call $env.eosio_assert
      local.get $l3
      return
    end
    local.get $l3)
  (func $f39 (type $t15) (param $p0 i32))
  (func $f40 (type $t0)
    (local $l0 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l0
    i32.const 0
    i32.store offset=12
    i32.const 0
    local.get $l0
    i32.load offset=12
    i32.load
    i32.const 7
    i32.add
    i32.const -8
    i32.and
    local.tee $l0
    i32.store offset=8196
    i32.const 0
    local.get $l0
    i32.store offset=8192
    i32.const 0
    memory.size
    i32.store offset=8204)
  (func $f41 (type $t15) (param $p0 i32))
  (func $f42 (type $t5) (result i32)
    i32.const 8208)
  (func $f43 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    i32.const 0
    local.set $l2
    block $B0
      block $B1
        i32.const 0
        local.get $p0
        i32.sub
        local.tee $l3
        local.get $p0
        i32.and
        local.get $p0
        i32.ne
        br_if $B1
        local.get $p0
        i32.const 16
        i32.gt_u
        br_if $B0
        local.get $p1
        call $f38
        return
      end
      call $f42
      i32.const 22
      i32.store
      i32.const 0
      return
    end
    block $B2
      block $B3
        block $B4
          local.get $p0
          i32.const -1
          i32.add
          local.tee $l4
          local.get $p1
          i32.add
          call $f38
          local.tee $p0
          i32.eqz
          br_if $B4
          local.get $p0
          local.get $l4
          local.get $p0
          i32.add
          local.get $l3
          i32.and
          local.tee $l2
          i32.eq
          br_if $B3
          local.get $p0
          i32.const -4
          i32.add
          local.tee $l3
          i32.load
          local.tee $l4
          i32.const 7
          i32.and
          local.tee $p1
          i32.eqz
          br_if $B2
          local.get $p0
          local.get $l4
          i32.const -8
          i32.and
          i32.add
          local.tee $l4
          i32.const -8
          i32.add
          local.tee $l5
          i32.load
          local.set $l6
          local.get $l3
          local.get $p1
          local.get $l2
          local.get $p0
          i32.sub
          local.tee $l7
          i32.or
          i32.store
          local.get $l2
          i32.const -4
          i32.add
          local.get $l4
          local.get $l2
          i32.sub
          local.tee $l3
          local.get $p1
          i32.or
          i32.store
          local.get $l2
          i32.const -8
          i32.add
          local.get $l6
          i32.const 7
          i32.and
          local.tee $p1
          local.get $l7
          i32.or
          i32.store
          local.get $l5
          local.get $p1
          local.get $l3
          i32.or
          i32.store
          local.get $p0
          call $f39
        end
        local.get $l2
        return
      end
      local.get $p0
      return
    end
    local.get $l2
    i32.const -8
    i32.add
    local.get $p0
    i32.const -8
    i32.add
    i32.load
    local.get $l2
    local.get $p0
    i32.sub
    local.tee $p0
    i32.add
    i32.store
    local.get $l2
    i32.const -4
    i32.add
    local.get $l3
    i32.load
    local.get $p0
    i32.sub
    i32.store
    local.get $l2)
  (func $f44 (type $t4) (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    (local $l3 i32)
    i32.const 22
    local.set $l3
    block $B0
      block $B1
        local.get $p1
        i32.const 4
        i32.lt_u
        br_if $B1
        local.get $p1
        local.get $p2
        call $f43
        local.tee $p1
        i32.eqz
        br_if $B0
        local.get $p0
        local.get $p1
        i32.store
        i32.const 0
        local.set $l3
      end
      local.get $l3
      return
    end
    call $f42
    i32.load)
  (func $f45 (type $t19) (param $p0 i32) (result i32)
    (local $l1 i32) (local $l2 i32)
    block $B0
      local.get $p0
      i32.const 1
      local.get $p0
      select
      local.tee $l1
      call $f38
      local.tee $p0
      br_if $B0
      loop $L1
        i32.const 0
        local.set $p0
        i32.const 0
        i32.load offset=8216
        local.tee $l2
        i32.eqz
        br_if $B0
        local.get $l2
        call_indirect (type $t0) $T0
        local.get $l1
        call $f38
        local.tee $p0
        i32.eqz
        br_if $L1
      end
    end
    local.get $p0)
  (func $f46 (type $t19) (param $p0 i32) (result i32)
    local.get $p0
    call $f45)
  (func $f47 (type $t15) (param $p0 i32)
    block $B0
      local.get $p0
      i32.eqz
      br_if $B0
      local.get $p0
      call $f39
    end)
  (func $f48 (type $t15) (param $p0 i32)
    local.get $p0
    call $f47)
  (func $f49 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    block $B0
      local.get $l2
      i32.const 12
      i32.add
      local.get $p1
      i32.const 4
      local.get $p1
      i32.const 4
      i32.gt_u
      select
      local.tee $p1
      local.get $p0
      i32.const 1
      local.get $p0
      select
      local.tee $l3
      call $f44
      i32.eqz
      br_if $B0
      block $B1
        loop $L2
          i32.const 0
          i32.load offset=8216
          local.tee $p0
          i32.eqz
          br_if $B1
          local.get $p0
          call_indirect (type $t0) $T0
          local.get $l2
          i32.const 12
          i32.add
          local.get $p1
          local.get $l3
          call $f44
          br_if $L2
          br $B0
        end
      end
      local.get $l2
      i32.const 0
      i32.store offset=12
    end
    local.get $l2
    i32.load offset=12
    local.set $p0
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0
    local.get $p0)
  (func $f50 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    local.get $p0
    local.get $p1
    call $f49)
  (func $f51 (type $t3) (param $p0 i32) (param $p1 i32)
    block $B0
      local.get $p0
      i32.eqz
      br_if $B0
      local.get $p0
      call $f39
    end)
  (func $f52 (type $t3) (param $p0 i32) (param $p1 i32)
    local.get $p0
    local.get $p1
    call $f51)
  (func $f53 (type $t15) (param $p0 i32)
    call $env.abort
    unreachable)
  (func $f54 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32)
    local.get $p0
    i64.const 0
    i64.store align=4
    local.get $p0
    i32.const 8
    i32.add
    local.tee $l2
    i32.const 0
    i32.store
    block $B0
      local.get $p1
      i32.load8_u
      i32.const 1
      i32.and
      br_if $B0
      local.get $p0
      local.get $p1
      i64.load align=4
      i64.store align=4
      local.get $l2
      local.get $p1
      i32.const 8
      i32.add
      i32.load
      i32.store
      local.get $p0
      return
    end
    block $B1
      local.get $p1
      i32.load offset=4
      local.tee $l2
      i32.const -16
      i32.ge_u
      br_if $B1
      local.get $p1
      i32.load offset=8
      local.set $l3
      block $B2
        block $B3
          local.get $l2
          i32.const 11
          i32.ge_u
          br_if $B3
          local.get $p0
          local.get $l2
          i32.const 1
          i32.shl
          i32.store8
          local.get $p0
          i32.const 1
          i32.add
          local.set $p1
          local.get $l2
          br_if $B2
          local.get $p1
          local.get $l2
          i32.add
          i32.const 0
          i32.store8
          local.get $p0
          return
        end
        local.get $l2
        i32.const 16
        i32.add
        i32.const -16
        i32.and
        local.tee $l4
        call $f45
        local.set $p1
        local.get $p0
        local.get $l4
        i32.const 1
        i32.or
        i32.store
        local.get $p0
        local.get $p1
        i32.store offset=8
        local.get $p0
        local.get $l2
        i32.store offset=4
      end
      local.get $p1
      local.get $l3
      local.get $l2
      call $env.memcpy
      drop
      local.get $p1
      local.get $l2
      i32.add
      i32.const 0
      i32.store8
      local.get $p0
      return
    end
    call $env.abort
    unreachable)
  (func $f55 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32)
    block $B0
      block $B1
        block $B2
          block $B3
            local.get $p1
            i32.const -16
            i32.ge_u
            br_if $B3
            block $B4
              block $B5
                local.get $p0
                i32.load8_u
                local.tee $l2
                i32.const 1
                i32.and
                br_if $B5
                local.get $l2
                i32.const 1
                i32.shr_u
                local.set $l3
                i32.const 10
                local.set $l4
                br $B4
              end
              local.get $p0
              i32.load
              local.tee $l2
              i32.const -2
              i32.and
              i32.const -1
              i32.add
              local.set $l4
              local.get $p0
              i32.load offset=4
              local.set $l3
            end
            i32.const 10
            local.set $l5
            block $B6
              local.get $l3
              local.get $p1
              local.get $l3
              local.get $p1
              i32.gt_u
              select
              local.tee $p1
              i32.const 11
              i32.lt_u
              br_if $B6
              local.get $p1
              i32.const 16
              i32.add
              i32.const -16
              i32.and
              i32.const -1
              i32.add
              local.set $l5
            end
            block $B7
              block $B8
                block $B9
                  local.get $l5
                  local.get $l4
                  i32.eq
                  br_if $B9
                  block $B10
                    local.get $l5
                    i32.const 10
                    i32.ne
                    br_if $B10
                    i32.const 1
                    local.set $l6
                    local.get $p0
                    i32.const 1
                    i32.add
                    local.set $p1
                    local.get $p0
                    i32.load offset=8
                    local.set $l4
                    i32.const 0
                    local.set $l7
                    i32.const 1
                    local.set $l8
                    local.get $l2
                    i32.const 1
                    i32.and
                    br_if $B7
                    br $B2
                  end
                  local.get $l5
                  i32.const 1
                  i32.add
                  call $f45
                  local.set $p1
                  local.get $l5
                  local.get $l4
                  i32.gt_u
                  br_if $B8
                  local.get $p1
                  br_if $B8
                end
                return
              end
              block $B11
                local.get $p0
                i32.load8_u
                local.tee $l2
                i32.const 1
                i32.and
                br_if $B11
                i32.const 1
                local.set $l7
                local.get $p0
                i32.const 1
                i32.add
                local.set $l4
                i32.const 0
                local.set $l6
                i32.const 1
                local.set $l8
                local.get $l2
                i32.const 1
                i32.and
                i32.eqz
                br_if $B2
                br $B7
              end
              local.get $p0
              i32.load offset=8
              local.set $l4
              i32.const 1
              local.set $l6
              i32.const 1
              local.set $l7
              i32.const 1
              local.set $l8
              local.get $l2
              i32.const 1
              i32.and
              i32.eqz
              br_if $B2
            end
            local.get $p0
            i32.load offset=4
            i32.const 1
            i32.add
            local.tee $l2
            i32.eqz
            br_if $B0
            br $B1
          end
          call $env.abort
          unreachable
        end
        local.get $l2
        i32.const 254
        i32.and
        local.get $l8
        i32.shr_u
        i32.const 1
        i32.add
        local.tee $l2
        i32.eqz
        br_if $B0
      end
      local.get $p1
      local.get $l4
      local.get $l2
      call $env.memcpy
      drop
    end
    block $B12
      local.get $l6
      i32.eqz
      br_if $B12
      local.get $l4
      call $f47
    end
    block $B13
      local.get $l7
      i32.eqz
      br_if $B13
      local.get $p0
      local.get $l3
      i32.store offset=4
      local.get $p0
      local.get $p1
      i32.store offset=8
      local.get $p0
      local.get $l5
      i32.const 1
      i32.add
      i32.const 1
      i32.or
      i32.store
      return
    end
    local.get $p0
    local.get $l3
    i32.const 1
    i32.shl
    i32.store8)
  (func $f56 (type $t15) (param $p0 i32)
    call $env.abort
    unreachable)
  (func $apply (type $t20) (param $p0 i64) (param $p1 i64) (param $p2 i64)
    (local $l3 i32)
    global.get $g0
    i32.const 112
    i32.sub
    local.tee $l3
    global.set $g0
    call $f36
    block $B0
      block $B1
        block $B2
          block $B3
            local.get $p1
            local.get $p0
            i64.ne
            br_if $B3
            local.get $p2
            i64.const -2404019103484706816
            i64.eq
            br_if $B2
            local.get $p2
            i64.const -2039333636196532224
            i64.eq
            br_if $B1
            local.get $p2
            i64.const 5380477996647841792
            i64.ne
            br_if $B0
            local.get $l3
            i32.const 0
            i32.store offset=108
            local.get $l3
            i32.const 1
            i32.store offset=104
            local.get $l3
            local.get $l3
            i64.load offset=104
            i64.store
            local.get $p1
            local.get $p1
            local.get $l3
            call $f59
            drop
            br $B0
          end
          local.get $l3
          i32.const 8245
          i32.store offset=72
          local.get $l3
          i32.const 8245
          call $f37
          i32.store offset=76
          local.get $l3
          local.get $l3
          i64.load offset=72
          i64.store offset=40
          local.get $l3
          i32.const 80
          i32.add
          local.get $l3
          i32.const 40
          i32.add
          call $f60
          drop
          local.get $p1
          i64.const 6138663591592764928
          i64.ne
          br_if $B0
          local.get $l3
          i32.const 8257
          i32.store offset=56
          local.get $l3
          i32.const 8257
          call $f37
          i32.store offset=60
          local.get $l3
          local.get $l3
          i64.load offset=56
          i64.store offset=32
          local.get $l3
          i32.const 64
          i32.add
          local.get $l3
          i32.const 32
          i32.add
          call $f60
          drop
          local.get $p2
          i64.const -3617168760277827584
          i64.ne
          br_if $B0
          local.get $l3
          i32.const 0
          i32.store offset=52
          local.get $l3
          i32.const 1
          i32.store offset=48
          local.get $l3
          local.get $l3
          i64.load offset=48
          i64.store offset=24
          local.get $p0
          i64.const 6138663591592764928
          local.get $l3
          i32.const 24
          i32.add
          call $f59
          drop
          br $B0
        end
        local.get $l3
        i32.const 0
        i32.store offset=92
        local.get $l3
        i32.const 2
        i32.store offset=88
        local.get $l3
        local.get $l3
        i64.load offset=88
        i64.store offset=16
        local.get $p1
        local.get $p1
        local.get $l3
        i32.const 16
        i32.add
        call $f62
        drop
        br $B0
      end
      local.get $l3
      i32.const 0
      i32.store offset=100
      local.get $l3
      i32.const 3
      i32.store offset=96
      local.get $l3
      local.get $l3
      i64.load offset=96
      i64.store offset=8
      local.get $p1
      local.get $p1
      local.get $l3
      i32.const 8
      i32.add
      call $f62
      drop
    end
    i32.const 0
    call $f41
    local.get $l3
    i32.const 112
    i32.add
    global.set $g0)
  (func $f58 (type $t2) (param $p0 i32) (param $p1 i64) (param $p2 i64) (param $p3 i32) (param $p4 i32)
    (local $l5 i32) (local $l6 i64) (local $l7 i32) (local $l8 i32)
    global.get $g0
    i32.const 64
    i32.sub
    local.tee $l5
    global.set $g0
    local.get $l5
    local.get $p1
    i64.store offset=56
    block $B0
      local.get $p0
      i64.load
      local.tee $l6
      local.get $p1
      i64.eq
      br_if $B0
      local.get $l5
      i32.const 48
      i32.add
      i32.const 0
      i32.store
      local.get $l5
      local.get $l6
      i64.store offset=24
      local.get $l5
      local.get $l6
      i64.store offset=16
      local.get $l5
      i64.const -1
      i64.store offset=32
      local.get $l5
      i64.const 0
      i64.store offset=40
      block $B1
        block $B2
          local.get $l6
          local.get $l6
          i64.const 4152997947673411584
          local.get $p1
          call $env.db_find_i64
          local.tee $l7
          i32.const 0
          i32.lt_s
          br_if $B2
          block $B3
            local.get $l5
            i32.const 16
            i32.add
            local.get $l7
            call $f66
            local.tee $l7
            i32.load offset=24
            local.get $l5
            i32.const 16
            i32.add
            i32.eq
            br_if $B3
            i32.const 0
            i32.const 8266
            call $env.eosio_assert
          end
          local.get $p0
          i64.load
          local.set $p1
          local.get $l5
          local.get $p3
          i32.store offset=8
          local.get $l5
          i32.const 16
          i32.add
          local.get $l7
          local.get $p1
          local.get $l5
          i32.const 8
          i32.add
          call $f67
          local.get $l5
          i32.load offset=40
          local.tee $l7
          br_if $B1
          br $B0
        end
        local.get $p0
        i64.load
        local.set $p1
        local.get $l5
        local.get $p3
        i32.store offset=12
        local.get $l5
        local.get $l5
        i32.const 56
        i32.add
        i32.store offset=8
        local.get $l5
        local.get $l5
        i32.const 16
        i32.add
        local.get $p1
        local.get $l5
        i32.const 8
        i32.add
        call $f68
        local.get $l5
        i32.load offset=40
        local.tee $l7
        i32.eqz
        br_if $B0
      end
      block $B4
        block $B5
          local.get $l5
          i32.const 44
          i32.add
          local.tee $l8
          i32.load
          local.tee $p0
          local.get $l7
          i32.eq
          br_if $B5
          loop $L6
            local.get $p0
            i32.const -24
            i32.add
            local.tee $p0
            i32.load
            local.set $p3
            local.get $p0
            i32.const 0
            i32.store
            block $B7
              local.get $p3
              i32.eqz
              br_if $B7
              local.get $p3
              call $f47
            end
            local.get $l7
            local.get $p0
            i32.ne
            br_if $L6
          end
          local.get $l5
          i32.const 40
          i32.add
          i32.load
          local.set $p0
          br $B4
        end
        local.get $l7
        local.set $p0
      end
      local.get $l8
      local.get $l7
      i32.store
      local.get $p0
      call $f47
      local.get $l5
      i32.const 64
      i32.add
      global.set $g0
      return
    end
    local.get $l5
    i32.const 64
    i32.add
    global.set $g0)
  (func $f59 (type $t21) (param $p0 i64) (param $p1 i64) (param $p2 i32) (result i32)
    (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i64)
    global.get $g0
    i32.const 176
    i32.sub
    local.tee $l3
    global.set $g0
    local.get $l3
    local.tee $l4
    local.get $p2
    i64.load align=4
    i64.store offset=120
    i32.const 0
    local.set $p2
    block $B0
      call $env.action_data_size
      local.tee $l5
      i32.eqz
      br_if $B0
      block $B1
        block $B2
          local.get $l5
          i32.const 513
          i32.lt_u
          br_if $B2
          local.get $l5
          call $f38
          local.set $p2
          br $B1
        end
        local.get $l3
        local.get $l5
        i32.const 15
        i32.add
        i32.const -16
        i32.and
        i32.sub
        local.tee $p2
        global.set $g0
      end
      local.get $p2
      local.get $l5
      call $env.read_action_data
      drop
    end
    local.get $l4
    i32.const 72
    i32.add
    i32.const 24
    i32.add
    i64.const 0
    i64.store
    local.get $l4
    i32.const 112
    i32.add
    i32.const 0
    i32.store
    local.get $l4
    i64.const 0
    i64.store offset=80
    local.get $l4
    i64.const 0
    i64.store offset=72
    local.get $l4
    i64.const 0
    i64.store offset=88
    local.get $l4
    i64.const 0
    i64.store offset=104
    local.get $l4
    local.get $p2
    i32.store offset=60
    local.get $l4
    local.get $p2
    i32.store offset=56
    local.get $l4
    local.get $p2
    local.get $l5
    i32.add
    i32.store offset=64
    local.get $l4
    local.get $l4
    i32.const 56
    i32.add
    i32.store offset=160
    local.get $l4
    local.get $l4
    i32.const 72
    i32.add
    i32.store offset=24
    local.get $l4
    i32.const 24
    i32.add
    local.get $l4
    i32.const 160
    i32.add
    call $f64
    local.get $l4
    i32.const 8
    i32.add
    i32.const 8
    i32.add
    local.tee $l3
    local.get $l4
    i32.load offset=64
    i32.store
    local.get $l4
    local.get $l4
    i64.load offset=56
    i64.store offset=8
    local.get $l4
    i32.const 128
    i32.add
    i32.const 8
    i32.add
    local.get $l3
    i32.load
    local.tee $l3
    i32.store
    local.get $l4
    i32.const 144
    i32.add
    i32.const 8
    i32.add
    local.tee $l6
    local.get $l3
    i32.store
    local.get $l4
    local.get $l4
    i64.load offset=8
    local.tee $l7
    i64.store offset=144
    local.get $l4
    local.get $l7
    i64.store offset=128
    local.get $l4
    i32.const 160
    i32.add
    i32.const 8
    i32.add
    local.get $l6
    i32.load
    local.tee $l3
    i32.store
    local.get $l4
    i32.const 24
    i32.add
    i32.const 24
    i32.add
    local.get $l3
    i32.store
    local.get $l4
    local.get $p0
    i64.store offset=24
    local.get $l4
    local.get $p1
    i64.store offset=32
    local.get $l4
    local.get $l4
    i64.load offset=144
    local.tee $p0
    i64.store offset=40
    local.get $l4
    local.get $p0
    i64.store offset=160
    local.get $l4
    local.get $l4
    i32.const 120
    i32.add
    i32.store offset=164
    local.get $l4
    local.get $l4
    i32.const 24
    i32.add
    i32.store offset=160
    local.get $l4
    i32.const 160
    i32.add
    local.get $l4
    i32.const 72
    i32.add
    call $f65
    block $B3
      block $B4
        block $B5
          local.get $l5
          i32.const 513
          i32.ge_u
          br_if $B5
          i32.const 1
          local.set $p2
          local.get $l4
          i32.load8_u offset=104
          i32.const 1
          i32.and
          br_if $B4
          br $B3
        end
        local.get $p2
        call $f39
        i32.const 1
        local.set $p2
        local.get $l4
        i32.load8_u offset=104
        i32.const 1
        i32.and
        i32.eqz
        br_if $B3
      end
      local.get $l4
      i32.const 112
      i32.add
      i32.load
      call $f47
      local.get $l4
      i32.const 176
      i32.add
      global.set $g0
      local.get $p2
      return
    end
    local.get $l4
    i32.const 176
    i32.add
    global.set $g0
    local.get $p2)
  (func $f60 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i64) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    local.get $p0
    i64.const 0
    i64.store
    block $B0
      block $B1
        block $B2
          block $B3
            block $B4
              local.get $p1
              i32.load offset=4
              local.tee $l2
              i32.const 14
              i32.lt_u
              br_if $B4
              i32.const 0
              i32.const 8887
              call $env.eosio_assert
              i32.const 12
              local.set $l3
              br $B3
            end
            local.get $l2
            i32.eqz
            br_if $B0
            local.get $l2
            i32.const 12
            local.get $l2
            i32.const 12
            i32.lt_u
            select
            local.tee $l3
            i32.eqz
            br_if $B2
          end
          local.get $p0
          i64.load
          local.set $l4
          local.get $p1
          i32.load
          local.set $l5
          i32.const 0
          local.set $l6
          loop $L5
            local.get $p0
            local.get $l4
            i64.const 5
            i64.shl
            local.tee $l4
            i64.store
            block $B6
              block $B7
                local.get $l5
                local.get $l6
                i32.add
                i32.load8_u
                local.tee $l7
                i32.const 46
                i32.ne
                br_if $B7
                i32.const 0
                local.set $l7
                br $B6
              end
              block $B8
                local.get $l7
                i32.const -49
                i32.add
                i32.const 255
                i32.and
                i32.const 4
                i32.gt_u
                br_if $B8
                local.get $l7
                i32.const -48
                i32.add
                local.set $l7
                br $B6
              end
              block $B9
                local.get $l7
                i32.const -97
                i32.add
                i32.const 255
                i32.and
                i32.const 25
                i32.gt_u
                br_if $B9
                local.get $l7
                i32.const -91
                i32.add
                local.set $l7
                br $B6
              end
              i32.const 0
              local.set $l7
              i32.const 0
              i32.const 8992
              call $env.eosio_assert
              local.get $p0
              i64.load
              local.set $l4
            end
            local.get $p0
            local.get $l4
            local.get $l7
            i64.extend_i32_u
            i64.const 255
            i64.and
            i64.or
            local.tee $l4
            i64.store
            local.get $l6
            i32.const 1
            i32.add
            local.tee $l6
            local.get $l3
            i32.lt_u
            br_if $L5
            br $B1
          end
        end
        local.get $p0
        i64.load
        local.set $l4
        i32.const 0
        local.set $l3
      end
      local.get $p0
      local.get $l4
      i32.const 12
      local.get $l3
      i32.sub
      i32.const 5
      i32.mul
      i32.const 4
      i32.add
      i64.extend_i32_u
      i64.shl
      i64.store
      local.get $l2
      i32.const 13
      i32.ne
      br_if $B0
      i64.const 0
      local.set $l4
      block $B10
        local.get $p1
        i32.load
        i32.load8_u offset=12
        local.tee $l6
        i32.const 46
        i32.eq
        br_if $B10
        block $B11
          local.get $l6
          i32.const -49
          i32.add
          i32.const 255
          i32.and
          i32.const 4
          i32.gt_u
          br_if $B11
          local.get $l6
          i32.const -48
          i32.add
          i64.extend_i32_u
          i64.const 255
          i64.and
          local.set $l4
          br $B10
        end
        block $B12
          local.get $l6
          i32.const -97
          i32.add
          i32.const 255
          i32.and
          i32.const 26
          i32.ge_u
          br_if $B12
          local.get $l6
          i32.const -91
          i32.add
          local.tee $l6
          i64.extend_i32_u
          i64.const 255
          i64.and
          local.set $l4
          local.get $l6
          i32.const 255
          i32.and
          i32.const 16
          i32.lt_u
          br_if $B10
          i32.const 0
          i32.const 8925
          call $env.eosio_assert
          br $B10
        end
        i32.const 0
        i32.const 8992
        call $env.eosio_assert
      end
      local.get $p0
      local.get $p0
      i64.load
      local.get $l4
      i64.or
      i64.store
    end
    local.get $p0)
  (func $f61 (type $t1) (param $p0 i32) (param $p1 i64)
    (local $l2 i32) (local $l3 i64) (local $l4 i32) (local $l5 i32) (local $l6 i32)
    global.get $g0
    i32.const 48
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $l2
    i32.const 40
    i32.add
    i32.const 0
    i32.store
    local.get $l2
    i64.const -1
    i64.store offset=24
    local.get $l2
    i64.const 0
    i64.store offset=32
    local.get $l2
    local.get $p0
    i64.load
    local.tee $l3
    i64.store offset=8
    local.get $l2
    local.get $l3
    i64.store offset=16
    block $B0
      block $B1
        block $B2
          local.get $l3
          local.get $l3
          i64.const 4152997947673411584
          local.get $p1
          call $env.db_find_i64
          local.tee $p0
          i32.const 0
          i32.lt_s
          br_if $B2
          block $B3
            local.get $l2
            i32.const 8
            i32.add
            local.get $p0
            call $f66
            i32.load offset=24
            local.get $l2
            i32.const 8
            i32.add
            i32.eq
            br_if $B3
            i32.const 0
            i32.const 8266
            call $env.eosio_assert
          end
          i32.const 8878
          call $env.prints
          local.get $l2
          i32.load offset=32
          local.tee $l4
          br_if $B1
          br $B0
        end
        i32.const 0
        i32.const 8638
        call $env.eosio_assert
        i32.const 8878
        call $env.prints
        local.get $l2
        i32.load offset=32
        local.tee $l4
        i32.eqz
        br_if $B0
      end
      block $B4
        block $B5
          local.get $l2
          i32.const 36
          i32.add
          local.tee $l5
          i32.load
          local.tee $p0
          local.get $l4
          i32.eq
          br_if $B5
          loop $L6
            local.get $p0
            i32.const -24
            i32.add
            local.tee $p0
            i32.load
            local.set $l6
            local.get $p0
            i32.const 0
            i32.store
            block $B7
              local.get $l6
              i32.eqz
              br_if $B7
              local.get $l6
              call $f47
            end
            local.get $l4
            local.get $p0
            i32.ne
            br_if $L6
          end
          local.get $l2
          i32.const 32
          i32.add
          i32.load
          local.set $p0
          br $B4
        end
        local.get $l4
        local.set $p0
      end
      local.get $l5
      local.get $l4
      i32.store
      local.get $p0
      call $f47
      local.get $l2
      i32.const 48
      i32.add
      global.set $g0
      return
    end
    local.get $l2
    i32.const 48
    i32.add
    global.set $g0)
  (func $f62 (type $t21) (param $p0 i64) (param $p1 i64) (param $p2 i32) (result i32)
    (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    global.get $g0
    i32.const 48
    i32.sub
    local.tee $l3
    local.set $l4
    local.get $l3
    global.set $g0
    local.get $p2
    i32.load offset=4
    local.set $l5
    local.get $p2
    i32.load
    local.set $l6
    block $B0
      block $B1
        block $B2
          block $B3
            call $env.action_data_size
            local.tee $l7
            i32.eqz
            br_if $B3
            local.get $l7
            i32.const 513
            i32.lt_u
            br_if $B2
            local.get $l7
            call $f38
            local.set $p2
            br $B1
          end
          i32.const 0
          local.set $p2
          br $B0
        end
        local.get $l3
        local.get $l7
        i32.const 15
        i32.add
        i32.const -16
        i32.and
        i32.sub
        local.tee $p2
        global.set $g0
      end
      local.get $p2
      local.get $l7
      call $env.read_action_data
      drop
    end
    local.get $l4
    i64.const 0
    i64.store offset=40
    local.get $p2
    local.get $l7
    i32.add
    local.set $l3
    block $B4
      local.get $l7
      i32.const 7
      i32.gt_u
      br_if $B4
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l4
    i32.const 40
    i32.add
    local.get $p2
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 28
    i32.add
    local.get $p2
    i32.const 8
    i32.add
    i32.store
    local.get $l4
    i32.const 32
    i32.add
    local.get $l3
    i32.store
    local.get $l4
    local.get $p1
    i64.store offset=16
    local.get $l4
    local.get $p0
    i64.store offset=8
    local.get $l4
    local.get $p2
    i32.store offset=24
    local.get $l4
    i32.const 8
    i32.add
    local.get $l5
    i32.const 1
    i32.shr_s
    i32.add
    local.set $l3
    local.get $l4
    i64.load offset=40
    local.set $p0
    block $B5
      local.get $l5
      i32.const 1
      i32.and
      i32.eqz
      br_if $B5
      local.get $l3
      i32.load
      local.get $l6
      i32.add
      i32.load
      local.set $l6
    end
    local.get $l3
    local.get $p0
    local.get $l6
    call_indirect (type $t1) $T0
    block $B6
      local.get $l7
      i32.const 513
      i32.lt_u
      br_if $B6
      local.get $p2
      call $f39
    end
    local.get $l4
    i32.const 48
    i32.add
    global.set $g0
    i32.const 1)
  (func $f63 (type $t1) (param $p0 i32) (param $p1 i64)
    (local $l2 i32) (local $l3 i32) (local $l4 i64) (local $l5 i32) (local $l6 i64) (local $l7 i32) (local $l8 i32) (local $l9 i32) (local $l10 i32) (local $l11 i32) (local $l12 i64)
    global.get $g0
    i32.const 256
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p1
    call $env.require_auth
    i32.const 0
    local.set $l3
    local.get $l2
    i32.const 208
    i32.add
    i32.const 0
    i32.store
    local.get $l2
    i64.const -1
    i64.store offset=192
    local.get $l2
    i64.const 0
    i64.store offset=200
    local.get $l2
    local.get $p0
    i64.load
    local.tee $l4
    i64.store offset=176
    local.get $l2
    local.get $l4
    i64.store offset=184
    block $B0
      block $B1
        local.get $l4
        local.get $l4
        i64.const 4152997947673411584
        local.get $p1
        call $env.db_find_i64
        local.tee $l5
        i32.const -1
        i32.le_s
        br_if $B1
        local.get $l2
        i32.const 176
        i32.add
        local.get $l5
        call $f66
        local.tee $l3
        i32.load offset=24
        local.get $l2
        i32.const 176
        i32.add
        i32.eq
        br_if $B0
        i32.const 0
        i32.const 8266
        call $env.eosio_assert
        br $B0
      end
      i32.const 0
      i32.const 8638
      call $env.eosio_assert
    end
    local.get $p0
    i64.load
    local.set $l4
    local.get $l2
    i32.const 8648
    i32.store offset=120
    local.get $l2
    i32.const 8648
    call $f37
    i32.store offset=124
    local.get $l2
    local.get $l2
    i64.load offset=120
    i64.store offset=16
    local.get $l2
    i32.const 128
    i32.add
    local.get $l2
    i32.const 16
    i32.add
    call $f60
    i64.load
    local.set $l6
    local.get $l2
    i32.const 8245
    i32.store offset=104
    local.get $l2
    i32.const 8245
    call $f37
    i32.store offset=108
    local.get $l2
    local.get $l2
    i64.load offset=104
    i64.store offset=8
    local.get $l2
    i32.const 112
    i32.add
    local.get $l2
    i32.const 8
    i32.add
    call $f60
    local.set $l7
    local.get $l2
    i32.const 8257
    i32.store offset=88
    local.get $l2
    i32.const 8257
    call $f37
    i32.store offset=92
    local.get $l2
    local.get $l2
    i64.load offset=88
    i64.store
    local.get $l2
    i32.const 96
    i32.add
    local.get $l2
    call $f60
    local.set $l8
    local.get $l2
    i32.const 24
    i32.add
    i32.const 8
    i32.add
    i32.const 0
    i32.store
    local.get $l2
    i64.const 0
    i64.store offset=24
    block $B2
      block $B3
        block $B4
          block $B5
            i32.const 8655
            call $f37
            local.tee $l5
            i32.const -16
            i32.ge_u
            br_if $B5
            local.get $l3
            i32.const 8
            i32.add
            local.set $l9
            block $B6
              block $B7
                block $B8
                  local.get $l5
                  i32.const 11
                  i32.ge_u
                  br_if $B8
                  local.get $l2
                  local.get $l5
                  i32.const 1
                  i32.shl
                  i32.store8 offset=24
                  local.get $l2
                  i32.const 24
                  i32.add
                  i32.const 1
                  i32.or
                  local.set $l10
                  local.get $l5
                  br_if $B7
                  br $B6
                end
                local.get $l5
                i32.const 16
                i32.add
                i32.const -16
                i32.and
                local.tee $l11
                call $f45
                local.set $l10
                local.get $l2
                local.get $l11
                i32.const 1
                i32.or
                i32.store offset=24
                local.get $l2
                local.get $l10
                i32.store offset=32
                local.get $l2
                local.get $l5
                i32.store offset=28
              end
              local.get $l10
              i32.const 8655
              local.get $l5
              call $env.memcpy
              drop
            end
            local.get $l10
            local.get $l5
            i32.add
            i32.const 0
            i32.store8
            local.get $l2
            local.get $p1
            i64.store offset=48
            local.get $l9
            i32.const 8
            i32.add
            i64.load
            local.set $p1
            local.get $l9
            i64.load
            local.set $l12
            local.get $l2
            i32.const 80
            i32.add
            local.get $l2
            i32.const 24
            i32.add
            i32.const 8
            i32.add
            local.tee $l5
            i32.load
            i32.store
            local.get $l5
            i32.const 0
            i32.store
            local.get $l2
            i32.const 40
            i32.add
            i32.const 24
            i32.add
            local.get $p1
            i64.store
            local.get $l2
            local.get $p0
            i64.load
            i64.store offset=40
            local.get $l2
            i64.load offset=24
            local.set $p1
            local.get $l2
            i64.const 0
            i64.store offset=24
            local.get $l2
            local.get $l7
            i64.load
            i64.store offset=136
            local.get $l2
            local.get $l8
            i64.load
            i64.store offset=144
            local.get $l2
            local.get $p1
            i64.store offset=72
            local.get $l2
            local.get $l12
            i64.store offset=56
            local.get $l2
            i32.const 0
            i32.store offset=152
            local.get $l2
            i32.const 156
            i32.add
            local.tee $p0
            i32.const 0
            i32.store
            local.get $l2
            i32.const 136
            i32.add
            i32.const 24
            i32.add
            local.tee $l10
            i32.const 0
            i32.store
            i32.const 16
            call $f45
            local.tee $l5
            local.get $l4
            i64.store
            local.get $l5
            local.get $l6
            i64.store offset=8
            local.get $l2
            i32.const 136
            i32.add
            i32.const 36
            i32.add
            i32.const 0
            i32.store
            local.get $l10
            local.get $l5
            i32.const 16
            i32.add
            local.tee $l9
            i32.store
            local.get $p0
            local.get $l9
            i32.store
            local.get $l2
            local.get $l5
            i32.store offset=152
            local.get $l2
            i64.const 0
            i64.store offset=164 align=4
            local.get $l2
            i32.const 40
            i32.add
            i32.const 36
            i32.add
            i32.load
            local.get $l2
            i32.load8_u offset=72
            local.tee $l5
            i32.const 1
            i32.shr_u
            local.get $l5
            i32.const 1
            i32.and
            select
            local.tee $p0
            i32.const 32
            i32.add
            local.set $l5
            local.get $p0
            i64.extend_i32_u
            local.set $p1
            local.get $l2
            i32.const 164
            i32.add
            local.set $p0
            loop $L9
              local.get $l5
              i32.const 1
              i32.add
              local.set $l5
              local.get $p1
              i64.const 7
              i64.shr_u
              local.tee $p1
              i64.const 0
              i64.ne
              br_if $L9
            end
            block $B10
              block $B11
                local.get $l5
                i32.eqz
                br_if $B11
                local.get $p0
                local.get $l5
                call $f69
                local.get $l2
                i32.const 168
                i32.add
                i32.load
                local.set $p0
                local.get $l2
                i32.const 164
                i32.add
                i32.load
                local.set $l5
                br $B10
              end
              i32.const 0
              local.set $p0
              i32.const 0
              local.set $l5
            end
            local.get $l2
            local.get $l5
            i32.store offset=244
            local.get $l2
            local.get $l5
            i32.store offset=240
            local.get $l2
            local.get $p0
            i32.store offset=248
            local.get $l2
            local.get $l2
            i32.const 240
            i32.add
            i32.store offset=216
            local.get $l2
            local.get $l2
            i32.const 40
            i32.add
            i32.store offset=224
            local.get $l2
            i32.const 224
            i32.add
            local.get $l2
            i32.const 216
            i32.add
            call $f70
            local.get $l2
            i32.const 0
            i32.store offset=232
            local.get $l2
            i64.const 0
            i64.store offset=224
            i32.const 16
            local.set $l5
            local.get $l2
            i32.const 156
            i32.add
            i32.load
            local.tee $p0
            local.get $l2
            i32.const 136
            i32.add
            i32.const 16
            i32.add
            i32.load
            local.tee $l10
            i32.sub
            local.tee $l9
            i32.const 4
            i32.shr_s
            i64.extend_i32_u
            local.set $p1
            loop $L12
              local.get $l5
              i32.const 1
              i32.add
              local.set $l5
              local.get $p1
              i64.const 7
              i64.shr_u
              local.tee $p1
              i64.const 0
              i64.ne
              br_if $L12
            end
            block $B13
              local.get $l10
              local.get $p0
              i32.eq
              br_if $B13
              local.get $l9
              i32.const -16
              i32.and
              local.get $l5
              i32.add
              local.set $l5
            end
            local.get $l5
            local.get $l2
            i32.const 168
            i32.add
            i32.load
            local.tee $p0
            i32.add
            local.get $l2
            i32.const 164
            i32.add
            i32.load
            local.tee $l10
            i32.sub
            local.set $l5
            local.get $p0
            local.get $l10
            i32.sub
            i64.extend_i32_u
            local.set $p1
            loop $L14
              local.get $l5
              i32.const 1
              i32.add
              local.set $l5
              local.get $p1
              i64.const 7
              i64.shr_u
              local.tee $p1
              i64.const 0
              i64.ne
              br_if $L14
            end
            block $B15
              block $B16
                local.get $l5
                i32.eqz
                br_if $B16
                local.get $l2
                i32.const 224
                i32.add
                local.get $l5
                call $f69
                local.get $l2
                i32.load offset=228
                local.set $p0
                local.get $l2
                i32.load offset=224
                local.set $l5
                br $B15
              end
              i32.const 0
              local.set $p0
              i32.const 0
              local.set $l5
            end
            local.get $l2
            local.get $l5
            i32.store offset=244
            local.get $l2
            local.get $l5
            i32.store offset=240
            local.get $l2
            local.get $p0
            i32.store offset=248
            local.get $l2
            i32.const 240
            i32.add
            local.get $l2
            i32.const 136
            i32.add
            call $f71
            drop
            local.get $l2
            i32.load offset=224
            local.tee $l5
            local.get $l2
            i32.load offset=228
            local.get $l5
            i32.sub
            call $env.send_inline
            block $B17
              local.get $l2
              i32.load offset=224
              local.tee $l5
              i32.eqz
              br_if $B17
              local.get $l2
              local.get $l5
              i32.store offset=228
              local.get $l5
              call $f47
            end
            block $B18
              local.get $l2
              i32.load offset=164
              local.tee $l5
              i32.eqz
              br_if $B18
              local.get $l2
              i32.const 168
              i32.add
              local.get $l5
              i32.store
              local.get $l5
              call $f47
            end
            block $B19
              local.get $l2
              i32.load offset=152
              local.tee $l5
              i32.eqz
              br_if $B19
              local.get $l2
              i32.const 156
              i32.add
              local.get $l5
              i32.store
              local.get $l5
              call $f47
            end
            block $B20
              block $B21
                local.get $l2
                i32.const 72
                i32.add
                i32.load8_u
                i32.const 1
                i32.and
                br_if $B21
                local.get $l2
                i32.load8_u offset=24
                i32.const 1
                i32.and
                br_if $B20
                br $B4
              end
              local.get $l2
              i32.const 80
              i32.add
              i32.load
              call $f47
              local.get $l2
              i32.load8_u offset=24
              i32.const 1
              i32.and
              i32.eqz
              br_if $B4
            end
            local.get $l2
            i32.const 32
            i32.add
            i32.load
            call $f47
            local.get $l3
            br_if $B2
            br $B3
          end
          local.get $l2
          i32.const 24
          i32.add
          call $f53
          unreachable
        end
        local.get $l3
        br_if $B2
      end
      i32.const 0
      i32.const 8666
      call $env.eosio_assert
      i32.const 0
      i32.const 8700
      call $env.eosio_assert
    end
    block $B22
      local.get $l3
      i32.load offset=28
      local.get $l2
      i32.const 40
      i32.add
      call $env.db_next_i64
      local.tee $l5
      i32.const 0
      i32.lt_s
      br_if $B22
      local.get $l2
      i32.const 176
      i32.add
      local.get $l5
      call $f66
      drop
    end
    local.get $l2
    i32.const 176
    i32.add
    local.get $l3
    call $f72
    block $B23
      local.get $l2
      i32.load offset=200
      local.tee $p0
      i32.eqz
      br_if $B23
      block $B24
        block $B25
          local.get $l2
          i32.const 204
          i32.add
          local.tee $l10
          i32.load
          local.tee $l5
          local.get $p0
          i32.eq
          br_if $B25
          loop $L26
            local.get $l5
            i32.const -24
            i32.add
            local.tee $l5
            i32.load
            local.set $l3
            local.get $l5
            i32.const 0
            i32.store
            block $B27
              local.get $l3
              i32.eqz
              br_if $B27
              local.get $l3
              call $f47
            end
            local.get $p0
            local.get $l5
            i32.ne
            br_if $L26
          end
          local.get $l2
          i32.const 200
          i32.add
          i32.load
          local.set $l5
          br $B24
        end
        local.get $p0
        local.set $l5
      end
      local.get $l10
      local.get $p0
      i32.store
      local.get $l5
      call $f47
    end
    local.get $l2
    i32.const 256
    i32.add
    global.set $g0)
  (func $f64 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p0
    i32.load
    local.set $l3
    block $B0
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $l5
      i32.sub
      i32.const 7
      i32.gt_u
      br_if $B0
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $l5
    end
    local.get $l3
    local.get $l5
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $l4
    local.get $l4
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $p0
    i32.load
    local.tee $l5
    i32.const 8
    i32.add
    local.set $l3
    block $B1
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $p0
      i32.sub
      i32.const 7
      i32.gt_u
      br_if $B1
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $p0
    end
    local.get $l3
    local.get $p0
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $l4
    local.get $l4
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $l5
    i32.const 16
    i32.add
    local.set $l3
    block $B2
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $p0
      i32.sub
      i32.const 7
      i32.gt_u
      br_if $B2
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $p0
    end
    local.get $l3
    local.get $p0
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $p0
    local.get $p0
    i32.load
    i32.const 8
    i32.add
    local.tee $l3
    i32.store
    local.get $l2
    i64.const 0
    i64.store offset=8
    block $B3
      local.get $l4
      i32.const 8
      i32.add
      i32.load
      local.get $l3
      i32.sub
      i32.const 7
      i32.gt_u
      br_if $B3
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
      local.get $p0
      i32.load
      local.set $l3
    end
    local.get $l2
    i32.const 8
    i32.add
    local.get $l3
    i32.const 8
    call $env.memcpy
    drop
    local.get $l5
    i32.const 24
    i32.add
    local.get $l2
    i64.load offset=8
    i64.store
    local.get $p0
    local.get $p0
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $p1
    i32.load
    local.get $l5
    i32.const 32
    i32.add
    call $f80
    drop
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0)
  (func $f65 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i64) (local $l5 i64) (local $l6 i32) (local $l7 i32)
    global.get $g0
    i32.const 96
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $l2
    i32.const 32
    i32.add
    i32.const 8
    i32.add
    local.tee $l3
    local.get $p1
    i32.const 24
    i32.add
    i64.load
    i64.store
    local.get $l2
    local.get $p1
    i64.load offset=16
    i64.store offset=32
    local.get $p1
    i64.load offset=8
    local.set $l4
    local.get $p1
    i64.load
    local.set $l5
    local.get $l2
    i32.const 16
    i32.add
    local.get $p1
    i32.const 32
    i32.add
    call $f54
    local.set $p1
    local.get $l2
    i32.const 48
    i32.add
    i32.const 8
    i32.add
    local.get $l3
    i64.load
    i64.store
    local.get $l2
    local.get $l2
    i64.load offset=32
    i64.store offset=48
    local.get $p0
    i32.load
    local.get $p0
    i32.load offset=4
    local.tee $p0
    i32.load offset=4
    local.tee $l6
    i32.const 1
    i32.shr_s
    i32.add
    local.set $l3
    local.get $p0
    i32.load
    local.set $p0
    block $B0
      local.get $l6
      i32.const 1
      i32.and
      i32.eqz
      br_if $B0
      local.get $l3
      i32.load
      local.get $p0
      i32.add
      i32.load
      local.set $p0
    end
    local.get $l2
    i32.const 80
    i32.add
    i32.const 8
    i32.add
    local.tee $l7
    local.get $l2
    i32.const 48
    i32.add
    i32.const 8
    i32.add
    i64.load
    i64.store
    local.get $l2
    local.get $l2
    i64.load offset=48
    i64.store offset=80
    local.get $l2
    i32.const 64
    i32.add
    local.get $p1
    call $f54
    local.set $l6
    local.get $l2
    i32.const 8
    i32.add
    local.get $l7
    i64.load
    i64.store
    local.get $l2
    local.get $l2
    i64.load offset=80
    i64.store
    local.get $l3
    local.get $l5
    local.get $l4
    local.get $l2
    local.get $l6
    local.get $p0
    call_indirect (type $t2) $T0
    block $B1
      block $B2
        block $B3
          local.get $l2
          i32.load8_u offset=64
          i32.const 1
          i32.and
          br_if $B3
          local.get $p1
          i32.load8_u
          i32.const 1
          i32.and
          br_if $B2
          br $B1
        end
        local.get $l6
        i32.load offset=8
        call $f47
        local.get $p1
        i32.load8_u
        i32.const 1
        i32.and
        i32.eqz
        br_if $B1
      end
      local.get $p1
      i32.load offset=8
      call $f47
      local.get $l2
      i32.const 96
      i32.add
      global.set $g0
      return
    end
    local.get $l2
    i32.const 96
    i32.add
    global.set $g0)
  (func $f66 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32) (local $l9 i64)
    global.get $g0
    i32.const 32
    i32.sub
    local.tee $l2
    local.set $l3
    local.get $l2
    global.set $g0
    block $B0
      local.get $p0
      i32.load offset=24
      local.tee $l4
      local.get $p0
      i32.const 28
      i32.add
      i32.load
      local.tee $l5
      i32.eq
      br_if $B0
      block $B1
        loop $L2
          local.get $l5
          i32.const -8
          i32.add
          i32.load
          local.get $p1
          i32.eq
          br_if $B1
          local.get $l4
          local.get $l5
          i32.const -24
          i32.add
          local.tee $l5
          i32.ne
          br_if $L2
          br $B0
        end
      end
      local.get $l4
      local.get $l5
      i32.eq
      br_if $B0
      local.get $l5
      i32.const -24
      i32.add
      i32.load
      local.set $l5
      local.get $l3
      i32.const 32
      i32.add
      global.set $g0
      local.get $l5
      return
    end
    block $B3
      block $B4
        block $B5
          local.get $p1
          i32.const 0
          i32.const 0
          call $env.db_get_i64
          local.tee $l4
          i32.const -1
          i32.le_s
          br_if $B5
          local.get $l4
          i32.const 513
          i32.ge_u
          br_if $B4
          local.get $l2
          local.get $l4
          i32.const 15
          i32.add
          i32.const -16
          i32.and
          i32.sub
          local.tee $l2
          global.set $g0
          i32.const 0
          local.set $l6
          br $B3
        end
        i32.const 0
        i32.const 8317
        call $env.eosio_assert
      end
      local.get $l4
      call $f38
      local.set $l2
      i32.const 1
      local.set $l6
    end
    local.get $p1
    local.get $l2
    local.get $l4
    call $env.db_get_i64
    drop
    i32.const 40
    call $f45
    local.tee $l5
    i64.const 0
    i64.store offset=8
    local.get $l5
    i64.const 0
    i64.store
    local.get $l5
    i64.const 0
    i64.store offset=16
    local.get $l5
    local.get $p0
    i32.store offset=24
    block $B6
      local.get $l4
      i32.const 7
      i32.gt_u
      br_if $B6
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l5
    i32.const 8
    i32.add
    local.set $l7
    local.get $l5
    local.get $l2
    i32.const 8
    call $env.memcpy
    drop
    local.get $l2
    i32.const 8
    i32.add
    local.set $l8
    block $B7
      local.get $l4
      i32.const -8
      i32.and
      local.tee $l4
      i32.const 8
      i32.ne
      br_if $B7
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l7
    local.get $l8
    i32.const 8
    call $env.memcpy
    drop
    local.get $l3
    i64.const 0
    i64.store offset=24
    local.get $l2
    i32.const 16
    i32.add
    local.set $l7
    block $B8
      local.get $l4
      i32.const 16
      i32.ne
      br_if $B8
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l3
    i32.const 24
    i32.add
    local.get $l7
    i32.const 8
    call $env.memcpy
    drop
    local.get $l5
    i32.const 16
    i32.add
    local.get $l3
    i64.load offset=24
    i64.store
    local.get $l5
    local.get $p1
    i32.store offset=28
    local.get $l3
    local.get $l5
    i32.store offset=16
    local.get $l3
    local.get $l5
    i64.load
    local.tee $l9
    i64.store offset=24
    local.get $l3
    local.get $p1
    i32.store offset=12
    block $B9
      block $B10
        block $B11
          local.get $p0
          i32.const 28
          i32.add
          local.tee $l7
          i32.load
          local.tee $l4
          local.get $p0
          i32.const 32
          i32.add
          i32.load
          i32.ge_u
          br_if $B11
          local.get $l4
          local.get $l9
          i64.store offset=8
          local.get $l4
          local.get $p1
          i32.store offset=16
          local.get $l3
          i32.const 0
          i32.store offset=16
          local.get $l4
          local.get $l5
          i32.store
          local.get $l7
          local.get $l4
          i32.const 24
          i32.add
          i32.store
          local.get $l6
          br_if $B10
          br $B9
        end
        local.get $p0
        i32.const 24
        i32.add
        local.get $l3
        i32.const 16
        i32.add
        local.get $l3
        i32.const 24
        i32.add
        local.get $l3
        i32.const 12
        i32.add
        call $f76
        local.get $l6
        i32.eqz
        br_if $B9
      end
      local.get $l2
      call $f39
    end
    local.get $l3
    i32.load offset=16
    local.set $p1
    local.get $l3
    i32.const 0
    i32.store offset=16
    block $B12
      local.get $p1
      i32.eqz
      br_if $B12
      local.get $p1
      call $f47
    end
    local.get $l3
    i32.const 32
    i32.add
    global.set $g0
    local.get $l5)
  (func $f67 (type $t22) (param $p0 i32) (param $p1 i32) (param $p2 i64) (param $p3 i32)
    (local $l4 i32) (local $l5 i32) (local $l6 i64) (local $l7 i64)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l4
    local.set $l5
    local.get $l4
    global.set $g0
    block $B0
      local.get $p1
      i32.load offset=24
      local.get $p0
      i32.eq
      br_if $B0
      i32.const 0
      i32.const 8345
      call $env.eosio_assert
    end
    block $B1
      call $env.current_receiver
      local.get $p0
      i64.load
      i64.eq
      br_if $B1
      i32.const 0
      i32.const 8391
      call $env.eosio_assert
    end
    local.get $p1
    i64.load
    local.set $l6
    block $B2
      local.get $p3
      i32.load
      local.tee $p3
      i64.load offset=8
      local.get $p1
      i32.const 16
      i32.add
      i64.load
      i64.eq
      br_if $B2
      i32.const 0
      i32.const 8501
      call $env.eosio_assert
    end
    local.get $p1
    local.get $p1
    i64.load offset=8
    local.get $p3
    i64.load
    i64.add
    local.tee $l7
    i64.store offset=8
    block $B3
      block $B4
        block $B5
          block $B6
            block $B7
              local.get $l7
              i64.const -4611686018427387904
              i64.le_s
              br_if $B7
              local.get $l7
              i64.const 4611686018427387904
              i64.ge_s
              br_if $B6
              br $B5
            end
            i32.const 0
            i32.const 8544
            call $env.eosio_assert
            local.get $p1
            i32.const 8
            i32.add
            i64.load
            i64.const 4611686018427387904
            i64.lt_s
            br_if $B5
          end
          i32.const 0
          i32.const 8563
          call $env.eosio_assert
          local.get $l6
          local.get $p1
          i64.load
          i64.ne
          br_if $B4
          br $B3
        end
        local.get $l6
        local.get $p1
        i64.load
        i64.eq
        br_if $B3
      end
      i32.const 0
      i32.const 8442
      call $env.eosio_assert
    end
    local.get $l4
    local.tee $p3
    i32.const -32
    i32.add
    local.tee $l4
    global.set $g0
    local.get $l5
    local.get $l4
    i32.store offset=4
    local.get $l5
    local.get $l4
    i32.store
    local.get $l5
    local.get $p3
    i32.const -8
    i32.add
    i32.store offset=8
    local.get $l5
    local.get $p1
    call $f75
    drop
    local.get $p1
    i32.load offset=28
    local.get $p2
    local.get $l4
    i32.const 24
    call $env.db_update_i64
    block $B8
      local.get $l6
      local.get $p0
      i64.load offset=16
      i64.lt_u
      br_if $B8
      local.get $p0
      i32.const 16
      i32.add
      i64.const -2
      local.get $l6
      i64.const 1
      i64.add
      local.get $l6
      i64.const -3
      i64.gt_u
      select
      i64.store
    end
    local.get $l5
    i32.const 16
    i32.add
    global.set $g0)
  (func $f68 (type $t22) (param $p0 i32) (param $p1 i32) (param $p2 i64) (param $p3 i32)
    (local $l4 i32) (local $l5 i32) (local $l6 i64) (local $l7 i32) (local $l8 i32)
    global.get $g0
    i32.const 64
    i32.sub
    local.tee $l4
    global.set $g0
    block $B0
      call $env.current_receiver
      local.get $p1
      i64.load
      i64.eq
      br_if $B0
      i32.const 0
      i32.const 8587
      call $env.eosio_assert
    end
    i32.const 40
    call $f45
    local.tee $l5
    i64.const 0
    i64.store offset=8
    local.get $l5
    i64.const 0
    i64.store
    local.get $l5
    i64.const 0
    i64.store offset=16
    local.get $l5
    local.get $p1
    i32.store offset=24
    local.get $l5
    local.get $p3
    i32.load
    i64.load
    i64.store
    local.get $l5
    local.get $p3
    i32.load offset=4
    local.tee $p3
    i64.load
    i64.store offset=8
    local.get $l5
    local.get $p3
    i32.const 8
    i32.add
    i64.load
    i64.store offset=16
    local.get $l4
    local.get $l4
    i32.const 16
    i32.add
    i32.const 24
    i32.add
    i32.store offset=56
    local.get $l4
    local.get $l4
    i32.const 16
    i32.add
    i32.store offset=52
    local.get $l4
    local.get $l4
    i32.const 16
    i32.add
    i32.store offset=48
    local.get $l4
    i32.const 48
    i32.add
    local.get $l5
    call $f75
    drop
    local.get $l5
    local.get $p1
    i64.load offset=8
    i64.const 4152997947673411584
    local.get $p2
    local.get $l5
    i64.load
    local.tee $l6
    local.get $l4
    i32.const 16
    i32.add
    i32.const 24
    call $env.db_store_i64
    local.tee $l7
    i32.store offset=28
    block $B1
      local.get $l6
      local.get $p1
      i64.load offset=16
      i64.lt_u
      br_if $B1
      local.get $p1
      i32.const 16
      i32.add
      i64.const -2
      local.get $l6
      i64.const 1
      i64.add
      local.get $l6
      i64.const -3
      i64.gt_u
      select
      i64.store
    end
    local.get $l4
    local.get $l5
    i32.store offset=48
    local.get $l4
    local.get $l5
    i64.load
    local.tee $l6
    i64.store offset=16
    local.get $l4
    local.get $l7
    i32.store offset=12
    block $B2
      block $B3
        local.get $p1
        i32.const 28
        i32.add
        local.tee $l8
        i32.load
        local.tee $p3
        local.get $p1
        i32.const 32
        i32.add
        i32.load
        i32.ge_u
        br_if $B3
        local.get $p3
        local.get $l6
        i64.store offset=8
        local.get $p3
        local.get $l7
        i32.store offset=16
        local.get $l4
        i32.const 0
        i32.store offset=48
        local.get $p3
        local.get $l5
        i32.store
        local.get $l8
        local.get $p3
        i32.const 24
        i32.add
        i32.store
        br $B2
      end
      local.get $p1
      i32.const 24
      i32.add
      local.get $l4
      i32.const 48
      i32.add
      local.get $l4
      i32.const 16
      i32.add
      local.get $l4
      i32.const 12
      i32.add
      call $f76
    end
    local.get $p0
    local.get $l5
    i32.store offset=4
    local.get $p0
    local.get $p1
    i32.store
    local.get $l4
    i32.load offset=48
    local.set $l5
    local.get $l4
    i32.const 0
    i32.store offset=48
    block $B4
      local.get $l5
      i32.eqz
      br_if $B4
      local.get $l5
      call $f47
    end
    local.get $l4
    i32.const 64
    i32.add
    global.set $g0)
  (func $f69 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    block $B0
      block $B1
        block $B2
          block $B3
            block $B4
              local.get $p0
              i32.load offset=8
              local.tee $l2
              local.get $p0
              i32.load offset=4
              local.tee $l3
              i32.sub
              local.get $p1
              i32.ge_u
              br_if $B4
              local.get $l3
              local.get $p0
              i32.load
              local.tee $l4
              i32.sub
              local.tee $l5
              local.get $p1
              i32.add
              local.tee $l6
              i32.const -1
              i32.le_s
              br_if $B2
              i32.const 2147483647
              local.set $l7
              block $B5
                local.get $l2
                local.get $l4
                i32.sub
                local.tee $l2
                i32.const 1073741822
                i32.gt_u
                br_if $B5
                local.get $l6
                local.get $l2
                i32.const 1
                i32.shl
                local.tee $l2
                local.get $l2
                local.get $l6
                i32.lt_u
                select
                local.tee $l7
                i32.eqz
                br_if $B3
              end
              local.get $l7
              call $f45
              local.set $l2
              br $B1
            end
            local.get $p0
            i32.const 4
            i32.add
            local.set $p0
            loop $L6
              local.get $l3
              i32.const 0
              i32.store8
              local.get $p0
              local.get $p0
              i32.load
              i32.const 1
              i32.add
              local.tee $l3
              i32.store
              local.get $p1
              i32.const -1
              i32.add
              local.tee $p1
              br_if $L6
              br $B0
            end
          end
          i32.const 0
          local.set $l7
          i32.const 0
          local.set $l2
          br $B1
        end
        local.get $p0
        call $f56
        unreachable
      end
      local.get $l2
      local.get $l7
      i32.add
      local.set $l7
      local.get $l3
      local.get $p1
      i32.add
      local.get $l4
      i32.sub
      local.set $l4
      local.get $l2
      local.get $l5
      i32.add
      local.tee $l5
      local.set $l3
      loop $L7
        local.get $l3
        i32.const 0
        i32.store8
        local.get $l3
        i32.const 1
        i32.add
        local.set $l3
        local.get $p1
        i32.const -1
        i32.add
        local.tee $p1
        br_if $L7
      end
      local.get $l2
      local.get $l4
      i32.add
      local.set $l4
      local.get $l5
      local.get $p0
      i32.const 4
      i32.add
      local.tee $l6
      i32.load
      local.get $p0
      i32.load
      local.tee $p1
      i32.sub
      local.tee $l3
      i32.sub
      local.set $l2
      block $B8
        local.get $l3
        i32.const 1
        i32.lt_s
        br_if $B8
        local.get $l2
        local.get $p1
        local.get $l3
        call $env.memcpy
        drop
        local.get $p0
        i32.load
        local.set $p1
      end
      local.get $p0
      local.get $l2
      i32.store
      local.get $l6
      local.get $l4
      i32.store
      local.get $p0
      i32.const 8
      i32.add
      local.get $l7
      i32.store
      local.get $p1
      i32.eqz
      br_if $B0
      local.get $p1
      call $f47
      return
    end)
  (func $f70 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p0
    i32.load
    local.set $l3
    block $B0
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $l5
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B0
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $l5
    end
    local.get $l5
    local.get $l3
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $l4
    local.get $l4
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $p0
    i32.load
    local.tee $l5
    i32.const 8
    i32.add
    local.set $l3
    block $B1
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $p0
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B1
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $p0
    end
    local.get $p0
    local.get $l3
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $l4
    local.get $l4
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $l5
    i32.const 16
    i32.add
    local.set $l3
    block $B2
      local.get $p1
      i32.load
      local.tee $l4
      i32.load offset=8
      local.get $l4
      i32.load offset=4
      local.tee $p0
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B2
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $l4
      i32.const 4
      i32.add
      i32.load
      local.set $p0
    end
    local.get $p0
    local.get $l3
    i32.const 8
    call $env.memcpy
    drop
    local.get $l4
    i32.const 4
    i32.add
    local.tee $p0
    local.get $p0
    i32.load
    i32.const 8
    i32.add
    local.tee $l3
    i32.store
    local.get $l2
    local.get $l5
    i32.const 24
    i32.add
    i64.load
    i64.store offset=8
    block $B3
      local.get $l4
      i32.const 8
      i32.add
      i32.load
      local.get $l3
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B3
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $p0
      i32.load
      local.set $l3
    end
    local.get $l3
    local.get $l2
    i32.const 8
    i32.add
    i32.const 8
    call $env.memcpy
    drop
    local.get $p0
    local.get $p0
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $p1
    i32.load
    local.get $l5
    i32.const 32
    i32.add
    call $f77
    drop
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0)
  (func $f71 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32)
    block $B0
      local.get $p0
      i32.load offset=8
      local.get $p0
      i32.load offset=4
      local.tee $l2
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B0
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $p0
      i32.const 4
      i32.add
      i32.load
      local.set $l2
    end
    local.get $l2
    local.get $p1
    i32.const 8
    call $env.memcpy
    drop
    local.get $p0
    i32.const 4
    i32.add
    local.tee $l2
    local.get $l2
    i32.load
    i32.const 8
    i32.add
    local.tee $l3
    i32.store
    local.get $p1
    i32.const 8
    i32.add
    local.set $l4
    block $B1
      local.get $p0
      i32.const 8
      i32.add
      i32.load
      local.get $l3
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B1
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $l2
      i32.load
      local.set $l3
    end
    local.get $l3
    local.get $l4
    i32.const 8
    call $env.memcpy
    drop
    local.get $l2
    local.get $l2
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $p0
    local.get $p1
    i32.const 16
    i32.add
    call $f78
    local.get $p1
    i32.const 28
    i32.add
    call $f79)
  (func $f72 (type $t3) (param $p0 i32) (param $p1 i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i64) (local $l7 i32) (local $l8 i32) (local $l9 i32)
    block $B0
      local.get $p1
      i32.load offset=24
      local.get $p0
      i32.eq
      br_if $B0
      i32.const 0
      i32.const 8730
      call $env.eosio_assert
    end
    block $B1
      call $env.current_receiver
      local.get $p0
      i64.load
      i64.eq
      br_if $B1
      i32.const 0
      i32.const 8775
      call $env.eosio_assert
    end
    block $B2
      block $B3
        block $B4
          block $B5
            block $B6
              block $B7
                local.get $p0
                i32.load offset=24
                local.tee $l2
                local.get $p0
                i32.const 28
                i32.add
                local.tee $l3
                i32.load
                local.tee $l4
                i32.eq
                br_if $B7
                local.get $l4
                local.set $l5
                block $B8
                  local.get $l4
                  i32.const -24
                  i32.add
                  i32.load
                  i64.load
                  local.get $p1
                  i64.load
                  local.tee $l6
                  i64.eq
                  br_if $B8
                  local.get $l2
                  i32.const 24
                  i32.add
                  local.set $l7
                  local.get $l4
                  local.set $l8
                  loop $L9
                    local.get $l7
                    local.get $l8
                    i32.eq
                    br_if $B7
                    local.get $l8
                    i32.const -48
                    i32.add
                    local.set $l9
                    local.get $l8
                    i32.const -24
                    i32.add
                    local.tee $l5
                    local.set $l8
                    local.get $l9
                    i32.load
                    i64.load
                    local.get $l6
                    i64.ne
                    br_if $L9
                  end
                end
                local.get $l2
                local.get $l5
                i32.eq
                br_if $B6
                i32.const -24
                local.set $l9
                local.get $l5
                local.get $l4
                i32.eq
                br_if $B5
                br $B4
              end
              local.get $l2
              local.set $l5
            end
            i32.const 0
            i32.const 8825
            call $env.eosio_assert
            i32.const -24
            local.set $l9
            local.get $l5
            local.get $l3
            i32.load
            local.tee $l4
            i32.ne
            br_if $B4
          end
          local.get $l5
          local.get $l9
          i32.add
          local.set $l7
          br $B3
        end
        local.get $l5
        local.set $l8
        loop $L10
          local.get $l8
          i32.load
          local.set $l7
          local.get $l8
          i32.const 0
          i32.store
          local.get $l8
          local.get $l9
          i32.add
          local.tee $l2
          i32.load
          local.set $l5
          local.get $l2
          local.get $l7
          i32.store
          block $B11
            local.get $l5
            i32.eqz
            br_if $B11
            local.get $l5
            call $f47
          end
          local.get $l8
          i32.const -8
          i32.add
          local.get $l8
          i32.const 16
          i32.add
          i32.load
          i32.store
          local.get $l8
          i32.const -16
          i32.add
          local.get $l8
          i32.const 8
          i32.add
          i64.load
          i64.store
          local.get $l4
          local.get $l8
          i32.const 24
          i32.add
          local.tee $l8
          i32.ne
          br_if $L10
        end
        local.get $l8
        i32.const -24
        i32.add
        local.set $l7
        local.get $p0
        i32.const 28
        i32.add
        i32.load
        local.tee $l5
        i32.const 24
        i32.add
        local.get $l8
        i32.eq
        br_if $B2
      end
      loop $L12
        local.get $l5
        local.get $l9
        i32.add
        local.tee $l5
        i32.load
        local.set $l8
        local.get $l5
        i32.const 0
        i32.store
        block $B13
          local.get $l8
          i32.eqz
          br_if $B13
          local.get $l8
          call $f47
        end
        local.get $l7
        local.get $l5
        i32.ne
        br_if $L12
      end
    end
    local.get $p0
    i32.const 28
    i32.add
    local.get $l7
    i32.store
    local.get $p1
    i32.load offset=28
    call $env.db_remove_i64)
  (func $f73 (type $t23) (param $p0 i64) (param $p1 i64)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32)
    global.get $g0
    i32.const 48
    i32.sub
    local.tee $l2
    local.set $l3
    local.get $l2
    global.set $g0
    block $B0
      block $B1
        block $B2
          block $B3
            call $env.action_data_size
            local.tee $l4
            i32.eqz
            br_if $B3
            local.get $l4
            i32.const 512
            i32.lt_u
            br_if $B2
            local.get $l4
            call $f38
            local.set $l2
            br $B1
          end
          i32.const 0
          local.set $l2
          br $B0
        end
        local.get $l2
        local.get $l4
        i32.const 15
        i32.add
        i32.const -16
        i32.and
        i32.sub
        local.tee $l2
        global.set $g0
      end
      local.get $l2
      local.get $l4
      call $env.read_action_data
      drop
    end
    local.get $l3
    i64.const 0
    i64.store offset=40
    local.get $l2
    local.get $l4
    i32.add
    local.set $l5
    block $B4
      local.get $l4
      i32.const 7
      i32.gt_u
      br_if $B4
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l3
    i32.const 40
    i32.add
    local.get $l2
    i32.const 8
    call $env.memcpy
    drop
    local.get $l3
    i32.const 28
    i32.add
    local.get $l2
    i32.const 8
    i32.add
    i32.store
    local.get $l3
    i32.const 32
    i32.add
    local.get $l5
    i32.store
    local.get $l3
    local.get $p1
    i64.store offset=16
    local.get $l3
    local.get $p0
    i64.store offset=8
    local.get $l3
    local.get $l2
    i32.store offset=24
    local.get $l3
    i32.const 8
    i32.add
    local.get $l3
    i64.load offset=40
    call $f61
    local.get $l3
    i32.const 48
    i32.add
    global.set $g0)
  (func $f74 (type $t23) (param $p0 i64) (param $p1 i64)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32)
    global.get $g0
    i32.const 48
    i32.sub
    local.tee $l2
    local.set $l3
    local.get $l2
    global.set $g0
    block $B0
      block $B1
        block $B2
          block $B3
            call $env.action_data_size
            local.tee $l4
            i32.eqz
            br_if $B3
            local.get $l4
            i32.const 512
            i32.lt_u
            br_if $B2
            local.get $l4
            call $f38
            local.set $l2
            br $B1
          end
          i32.const 0
          local.set $l2
          br $B0
        end
        local.get $l2
        local.get $l4
        i32.const 15
        i32.add
        i32.const -16
        i32.and
        i32.sub
        local.tee $l2
        global.set $g0
      end
      local.get $l2
      local.get $l4
      call $env.read_action_data
      drop
    end
    local.get $l3
    i64.const 0
    i64.store offset=40
    local.get $l2
    local.get $l4
    i32.add
    local.set $l5
    block $B4
      local.get $l4
      i32.const 7
      i32.gt_u
      br_if $B4
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
    end
    local.get $l3
    i32.const 40
    i32.add
    local.get $l2
    i32.const 8
    call $env.memcpy
    drop
    local.get $l3
    i32.const 28
    i32.add
    local.get $l2
    i32.const 8
    i32.add
    i32.store
    local.get $l3
    i32.const 32
    i32.add
    local.get $l5
    i32.store
    local.get $l3
    local.get $p1
    i64.store offset=16
    local.get $l3
    local.get $p0
    i64.store offset=8
    local.get $l3
    local.get $l2
    i32.store offset=24
    local.get $l3
    i32.const 8
    i32.add
    local.get $l3
    i64.load offset=40
    call $f63
    local.get $l3
    i32.const 48
    i32.add
    global.set $g0)
  (func $f75 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    block $B0
      local.get $p0
      i32.load offset=8
      local.get $p0
      i32.load offset=4
      local.tee $l3
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B0
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $p0
      i32.const 4
      i32.add
      i32.load
      local.set $l3
    end
    local.get $p1
    i32.const 8
    i32.add
    local.set $l4
    local.get $l3
    local.get $p1
    i32.const 8
    call $env.memcpy
    drop
    local.get $p0
    i32.const 4
    i32.add
    local.tee $l3
    local.get $l3
    i32.load
    i32.const 8
    i32.add
    local.tee $l5
    i32.store
    block $B1
      local.get $p0
      i32.const 8
      i32.add
      i32.load
      local.get $l5
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B1
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $l3
      i32.load
      local.set $l5
    end
    local.get $l5
    local.get $l4
    i32.const 8
    call $env.memcpy
    drop
    local.get $l3
    local.get $l3
    i32.load
    i32.const 8
    i32.add
    local.tee $l5
    i32.store
    local.get $l2
    local.get $p1
    i32.const 16
    i32.add
    i64.load
    i64.store offset=8
    block $B2
      local.get $p0
      i32.const 8
      i32.add
      i32.load
      local.get $l5
      i32.sub
      i32.const 7
      i32.gt_s
      br_if $B2
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $p0
      i32.const 4
      i32.add
      i32.load
      local.set $l5
    end
    local.get $l5
    local.get $l2
    i32.const 8
    i32.add
    i32.const 8
    call $env.memcpy
    drop
    local.get $p0
    i32.const 4
    i32.add
    local.tee $l3
    local.get $l3
    i32.load
    i32.const 8
    i32.add
    i32.store
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0
    local.get $p0)
  (func $f76 (type $t24) (param $p0 i32) (param $p1 i32) (param $p2 i32) (param $p3 i32)
    (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32)
    block $B0
      block $B1
        local.get $p0
        i32.load offset=4
        local.get $p0
        i32.load
        local.tee $l4
        i32.sub
        i32.const 24
        i32.div_s
        local.tee $l5
        i32.const 1
        i32.add
        local.tee $l6
        i32.const 178956971
        i32.ge_u
        br_if $B1
        i32.const 178956970
        local.set $l7
        block $B2
          block $B3
            local.get $p0
            i32.load offset=8
            local.get $l4
            i32.sub
            i32.const 24
            i32.div_s
            local.tee $l4
            i32.const 89478484
            i32.gt_u
            br_if $B3
            local.get $l6
            local.get $l4
            i32.const 1
            i32.shl
            local.tee $l7
            local.get $l7
            local.get $l6
            i32.lt_u
            select
            local.tee $l7
            i32.eqz
            br_if $B2
          end
          local.get $l7
          i32.const 24
          i32.mul
          call $f45
          local.set $l4
          br $B0
        end
        i32.const 0
        local.set $l7
        i32.const 0
        local.set $l4
        br $B0
      end
      local.get $p0
      call $f56
      unreachable
    end
    local.get $p1
    i32.load
    local.set $l6
    local.get $p1
    i32.const 0
    i32.store
    local.get $l4
    local.get $l5
    i32.const 24
    i32.mul
    local.tee $l8
    i32.add
    local.tee $p1
    local.get $l6
    i32.store
    local.get $p1
    local.get $p2
    i64.load
    i64.store offset=8
    local.get $p1
    local.get $p3
    i32.load
    i32.store offset=16
    local.get $l4
    local.get $l7
    i32.const 24
    i32.mul
    i32.add
    local.set $l5
    local.get $p1
    i32.const 24
    i32.add
    local.set $l6
    block $B4
      block $B5
        local.get $p0
        i32.const 4
        i32.add
        i32.load
        local.tee $p2
        local.get $p0
        i32.load
        local.tee $l7
        i32.eq
        br_if $B5
        local.get $l4
        local.get $l8
        i32.add
        i32.const -24
        i32.add
        local.set $p1
        loop $L6
          local.get $p2
          i32.const -24
          i32.add
          local.tee $l4
          i32.load
          local.set $p3
          local.get $l4
          i32.const 0
          i32.store
          local.get $p1
          local.get $p3
          i32.store
          local.get $p1
          i32.const 16
          i32.add
          local.get $p2
          i32.const -8
          i32.add
          i32.load
          i32.store
          local.get $p1
          i32.const 8
          i32.add
          local.get $p2
          i32.const -16
          i32.add
          i64.load
          i64.store
          local.get $p1
          i32.const -24
          i32.add
          local.set $p1
          local.get $l4
          local.set $p2
          local.get $l7
          local.get $l4
          i32.ne
          br_if $L6
        end
        local.get $p1
        i32.const 24
        i32.add
        local.set $p1
        local.get $p0
        i32.const 4
        i32.add
        i32.load
        local.set $l7
        local.get $p0
        i32.load
        local.set $p2
        br $B4
      end
      local.get $l7
      local.set $p2
    end
    local.get $p0
    local.get $p1
    i32.store
    local.get $p0
    i32.const 4
    i32.add
    local.get $l6
    i32.store
    local.get $p0
    i32.const 8
    i32.add
    local.get $l5
    i32.store
    block $B7
      local.get $l7
      local.get $p2
      i32.eq
      br_if $B7
      loop $L8
        local.get $l7
        i32.const -24
        i32.add
        local.tee $l7
        i32.load
        local.set $p1
        local.get $l7
        i32.const 0
        i32.store
        block $B9
          local.get $p1
          i32.eqz
          br_if $B9
          local.get $p1
          call $f47
        end
        local.get $p2
        local.get $l7
        i32.ne
        br_if $L8
      end
    end
    block $B10
      local.get $p2
      i32.eqz
      br_if $B10
      local.get $p2
      call $f47
    end)
  (func $f77 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i64) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p1
    i32.load offset=4
    local.get $p1
    i32.load8_u
    local.tee $l3
    i32.const 1
    i32.shr_u
    local.get $l3
    i32.const 1
    i32.and
    select
    i64.extend_i32_u
    local.set $l4
    local.get $p0
    i32.load offset=4
    local.set $l3
    local.get $p0
    i32.const 8
    i32.add
    local.set $l5
    local.get $p0
    i32.const 4
    i32.add
    local.set $l6
    loop $L0
      local.get $l4
      i32.wrap_i64
      local.set $l7
      local.get $l2
      local.get $l4
      i64.const 7
      i64.shr_u
      local.tee $l4
      i64.const 0
      i64.ne
      local.tee $l8
      i32.const 7
      i32.shl
      local.get $l7
      i32.const 127
      i32.and
      i32.or
      i32.store8 offset=15
      block $B1
        local.get $l5
        i32.load
        local.get $l3
        i32.sub
        i32.const 0
        i32.gt_s
        br_if $B1
        i32.const 0
        i32.const 8581
        call $env.eosio_assert
        local.get $l6
        i32.load
        local.set $l3
      end
      local.get $l3
      local.get $l2
      i32.const 15
      i32.add
      i32.const 1
      call $env.memcpy
      drop
      local.get $l6
      local.get $l6
      i32.load
      i32.const 1
      i32.add
      local.tee $l3
      i32.store
      local.get $l8
      br_if $L0
    end
    block $B2
      local.get $p1
      i32.const 4
      i32.add
      i32.load
      local.get $p1
      i32.load8_u
      local.tee $l6
      i32.const 1
      i32.shr_u
      local.get $l6
      i32.const 1
      i32.and
      local.tee $l7
      select
      local.tee $l6
      i32.eqz
      br_if $B2
      local.get $p1
      i32.load offset=8
      local.get $p1
      i32.const 1
      i32.add
      local.get $l7
      select
      local.set $l7
      block $B3
        local.get $p0
        i32.const 8
        i32.add
        i32.load
        local.get $l3
        i32.sub
        local.get $l6
        i32.ge_s
        br_if $B3
        i32.const 0
        i32.const 8581
        call $env.eosio_assert
        local.get $p0
        i32.const 4
        i32.add
        i32.load
        local.set $l3
      end
      local.get $l3
      local.get $l7
      local.get $l6
      call $env.memcpy
      drop
      local.get $p0
      i32.const 4
      i32.add
      local.tee $l3
      local.get $l3
      i32.load
      local.get $l6
      i32.add
      i32.store
    end
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0
    local.get $p0)
  (func $f78 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i64) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p1
    i32.load offset=4
    local.get $p1
    i32.load
    i32.sub
    i32.const 4
    i32.shr_s
    i64.extend_i32_u
    local.set $l3
    local.get $p0
    i32.load offset=4
    local.set $l4
    local.get $p0
    i32.const 8
    i32.add
    local.set $l5
    loop $L0
      local.get $l3
      i32.wrap_i64
      local.set $l6
      local.get $l2
      local.get $l3
      i64.const 7
      i64.shr_u
      local.tee $l3
      i64.const 0
      i64.ne
      local.tee $l7
      i32.const 7
      i32.shl
      local.get $l6
      i32.const 127
      i32.and
      i32.or
      i32.store8 offset=15
      block $B1
        local.get $l5
        i32.load
        local.get $l4
        i32.sub
        i32.const 0
        i32.gt_s
        br_if $B1
        i32.const 0
        i32.const 8581
        call $env.eosio_assert
        local.get $p0
        i32.const 4
        i32.add
        i32.load
        local.set $l4
      end
      local.get $l4
      local.get $l2
      i32.const 15
      i32.add
      i32.const 1
      call $env.memcpy
      drop
      local.get $p0
      i32.const 4
      i32.add
      local.tee $l4
      local.get $l4
      i32.load
      i32.const 1
      i32.add
      local.tee $l4
      i32.store
      local.get $l7
      br_if $L0
    end
    block $B2
      local.get $p1
      i32.load
      local.tee $l7
      local.get $p1
      i32.const 4
      i32.add
      i32.load
      local.tee $p1
      i32.eq
      br_if $B2
      local.get $p0
      i32.const 8
      i32.add
      local.set $l5
      local.get $p0
      i32.const 4
      i32.add
      local.set $l6
      loop $L3
        block $B4
          local.get $l5
          i32.load
          local.get $l4
          i32.sub
          i32.const 7
          i32.gt_s
          br_if $B4
          i32.const 0
          i32.const 8581
          call $env.eosio_assert
          local.get $l6
          i32.load
          local.set $l4
        end
        local.get $l4
        local.get $l7
        i32.const 8
        call $env.memcpy
        drop
        local.get $l6
        local.get $l6
        i32.load
        i32.const 8
        i32.add
        local.tee $l4
        i32.store
        block $B5
          local.get $l5
          i32.load
          local.get $l4
          i32.sub
          i32.const 7
          i32.gt_s
          br_if $B5
          i32.const 0
          i32.const 8581
          call $env.eosio_assert
          local.get $l6
          i32.load
          local.set $l4
        end
        local.get $l4
        local.get $l7
        i32.const 8
        i32.add
        i32.const 8
        call $env.memcpy
        drop
        local.get $l6
        local.get $l6
        i32.load
        i32.const 8
        i32.add
        local.tee $l4
        i32.store
        local.get $l7
        i32.const 16
        i32.add
        local.tee $l7
        local.get $p1
        i32.ne
        br_if $L3
      end
    end
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0
    local.get $p0)
  (func $f79 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i64) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32)
    global.get $g0
    i32.const 16
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $p1
    i32.load offset=4
    local.get $p1
    i32.load
    i32.sub
    i64.extend_i32_u
    local.set $l3
    local.get $p0
    i32.load offset=4
    local.set $l4
    local.get $p0
    i32.const 8
    i32.add
    local.set $l5
    local.get $p0
    i32.const 4
    i32.add
    local.set $l6
    loop $L0
      local.get $l3
      i32.wrap_i64
      local.set $l7
      local.get $l2
      local.get $l3
      i64.const 7
      i64.shr_u
      local.tee $l3
      i64.const 0
      i64.ne
      local.tee $l8
      i32.const 7
      i32.shl
      local.get $l7
      i32.const 127
      i32.and
      i32.or
      i32.store8 offset=15
      block $B1
        local.get $l5
        i32.load
        local.get $l4
        i32.sub
        i32.const 0
        i32.gt_s
        br_if $B1
        i32.const 0
        i32.const 8581
        call $env.eosio_assert
        local.get $l6
        i32.load
        local.set $l4
      end
      local.get $l4
      local.get $l2
      i32.const 15
      i32.add
      i32.const 1
      call $env.memcpy
      drop
      local.get $l6
      local.get $l6
      i32.load
      i32.const 1
      i32.add
      local.tee $l4
      i32.store
      local.get $l8
      br_if $L0
    end
    block $B2
      local.get $p0
      i32.const 8
      i32.add
      i32.load
      local.get $l4
      i32.sub
      local.get $p1
      i32.const 4
      i32.add
      i32.load
      local.get $p1
      i32.load
      local.tee $l7
      i32.sub
      local.tee $l6
      i32.ge_s
      br_if $B2
      i32.const 0
      i32.const 8581
      call $env.eosio_assert
      local.get $p0
      i32.const 4
      i32.add
      i32.load
      local.set $l4
    end
    local.get $l4
    local.get $l7
    local.get $l6
    call $env.memcpy
    drop
    local.get $p0
    i32.const 4
    i32.add
    local.tee $l4
    local.get $l4
    i32.load
    local.get $l6
    i32.add
    i32.store
    local.get $l2
    i32.const 16
    i32.add
    global.set $g0
    local.get $p0)
  (func $f80 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i32) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32)
    global.get $g0
    i32.const 32
    i32.sub
    local.tee $l2
    global.set $g0
    local.get $l2
    i32.const 0
    i32.store offset=24
    local.get $l2
    i64.const 0
    i64.store offset=16
    local.get $p0
    local.get $l2
    i32.const 16
    i32.add
    call $f81
    drop
    block $B0
      block $B1
        block $B2
          block $B3
            block $B4
              block $B5
                block $B6
                  block $B7
                    local.get $l2
                    i32.load offset=20
                    local.get $l2
                    i32.load offset=16
                    local.tee $l3
                    i32.sub
                    local.tee $l4
                    i32.eqz
                    br_if $B7
                    local.get $l2
                    i32.const 8
                    i32.add
                    i32.const 0
                    i32.store
                    local.get $l2
                    i64.const 0
                    i64.store
                    local.get $l4
                    i32.const -16
                    i32.ge_u
                    br_if $B2
                    local.get $l4
                    i32.const 10
                    i32.gt_u
                    br_if $B6
                    local.get $l2
                    local.get $l4
                    i32.const 1
                    i32.shl
                    i32.store8
                    local.get $l2
                    i32.const 1
                    i32.or
                    local.set $l5
                    br $B5
                  end
                  local.get $p1
                  i32.load8_u
                  i32.const 1
                  i32.and
                  br_if $B4
                  local.get $p1
                  i32.const 0
                  i32.store16
                  local.get $p1
                  i32.const 8
                  i32.add
                  local.set $l3
                  br $B3
                end
                local.get $l4
                i32.const 16
                i32.add
                i32.const -16
                i32.and
                local.tee $l6
                call $f45
                local.set $l5
                local.get $l2
                local.get $l6
                i32.const 1
                i32.or
                i32.store
                local.get $l2
                local.get $l5
                i32.store offset=8
                local.get $l2
                local.get $l4
                i32.store offset=4
              end
              local.get $l4
              local.set $l7
              local.get $l5
              local.set $l6
              loop $L8
                local.get $l6
                local.get $l3
                i32.load8_u
                i32.store8
                local.get $l6
                i32.const 1
                i32.add
                local.set $l6
                local.get $l3
                i32.const 1
                i32.add
                local.set $l3
                local.get $l7
                i32.const -1
                i32.add
                local.tee $l7
                br_if $L8
              end
              local.get $l5
              local.get $l4
              i32.add
              i32.const 0
              i32.store8
              block $B9
                block $B10
                  local.get $p1
                  i32.load8_u
                  i32.const 1
                  i32.and
                  br_if $B10
                  local.get $p1
                  i32.const 0
                  i32.store16
                  br $B9
                end
                local.get $p1
                i32.load offset=8
                i32.const 0
                i32.store8
                local.get $p1
                i32.const 0
                i32.store offset=4
              end
              local.get $p1
              i32.const 0
              call $f55
              local.get $p1
              i32.const 8
              i32.add
              local.get $l2
              i32.const 8
              i32.add
              i32.load
              i32.store
              local.get $p1
              local.get $l2
              i64.load
              i64.store align=4
              local.get $l2
              i32.load offset=16
              local.tee $l3
              i32.eqz
              br_if $B0
              br $B1
            end
            local.get $p1
            i32.load offset=8
            i32.const 0
            i32.store8
            local.get $p1
            i32.const 0
            i32.store offset=4
            local.get $p1
            i32.const 8
            i32.add
            local.set $l3
          end
          local.get $p1
          i32.const 0
          call $f55
          local.get $l3
          i32.const 0
          i32.store
          local.get $p1
          i64.const 0
          i64.store align=4
          local.get $l2
          i32.load offset=16
          local.tee $l3
          br_if $B1
          br $B0
        end
        local.get $l2
        call $f53
        unreachable
      end
      local.get $l2
      local.get $l3
      i32.store offset=20
      local.get $l3
      call $f47
    end
    local.get $l2
    i32.const 32
    i32.add
    global.set $g0
    local.get $p0)
  (func $f81 (type $t6) (param $p0 i32) (param $p1 i32) (result i32)
    (local $l2 i32) (local $l3 i64) (local $l4 i32) (local $l5 i32) (local $l6 i32) (local $l7 i32) (local $l8 i32)
    local.get $p0
    i32.load offset=4
    local.set $l2
    i64.const 0
    local.set $l3
    local.get $p0
    i32.const 8
    i32.add
    local.set $l4
    local.get $p0
    i32.const 4
    i32.add
    local.set $l5
    i32.const 0
    local.set $l6
    loop $L0
      block $B1
        local.get $l2
        local.get $l4
        i32.load
        i32.lt_u
        br_if $B1
        i32.const 0
        i32.const 9044
        call $env.eosio_assert
        local.get $l5
        i32.load
        local.set $l2
      end
      local.get $l2
      i32.load8_u
      local.set $l7
      local.get $l5
      local.get $l2
      i32.const 1
      i32.add
      local.tee $l8
      i32.store
      local.get $l3
      local.get $l7
      i32.const 127
      i32.and
      local.get $l6
      i32.const 255
      i32.and
      local.tee $l2
      i32.shl
      i64.extend_i32_u
      i64.or
      local.set $l3
      local.get $l2
      i32.const 7
      i32.add
      local.set $l6
      local.get $l8
      local.set $l2
      local.get $l7
      i32.const 128
      i32.and
      br_if $L0
    end
    block $B2
      block $B3
        local.get $p1
        i32.load offset=4
        local.tee $l7
        local.get $p1
        i32.load
        local.tee $l2
        i32.sub
        local.tee $l5
        local.get $l3
        i32.wrap_i64
        local.tee $l6
        i32.ge_u
        br_if $B3
        local.get $p1
        local.get $l6
        local.get $l5
        i32.sub
        call $f69
        local.get $p0
        i32.const 4
        i32.add
        i32.load
        local.set $l8
        local.get $p1
        i32.const 4
        i32.add
        i32.load
        local.set $l7
        local.get $p1
        i32.load
        local.set $l2
        br $B2
      end
      local.get $l5
      local.get $l6
      i32.le_u
      br_if $B2
      local.get $p1
      i32.const 4
      i32.add
      local.get $l2
      local.get $l6
      i32.add
      local.tee $l7
      i32.store
    end
    block $B4
      local.get $p0
      i32.const 8
      i32.add
      i32.load
      local.get $l8
      i32.sub
      local.get $l7
      local.get $l2
      i32.sub
      local.tee $l7
      i32.ge_u
      br_if $B4
      i32.const 0
      i32.const 8340
      call $env.eosio_assert
      local.get $p0
      i32.const 4
      i32.add
      i32.load
      local.set $l8
    end
    local.get $l2
    local.get $l8
    local.get $l7
    call $env.memcpy
    drop
    local.get $p0
    i32.const 4
    i32.add
    local.tee $l2
    local.get $l2
    i32.load
    local.get $l7
    i32.add
    i32.store
    local.get $p0)
  (table $T0 4 4 funcref)
  (memory $M0 1)
  (global $g0 (mut i32) (i32.const 8192))
  (global $g1 i32 (i32.const 9048))
  (global $g2 i32 (i32.const 9048))
  (export "apply" (func $apply))
  (elem $e0 (i32.const 1) $f58 $f61 $f63)
  (data $d0 (i32.const 8220) "failed to allocate pages\00eosio.token\00")
  (data $d1 (i32.const 8257) "transfer\00")
  (data $d2 (i32.const 8266) "object passed to iterator_to is not in multi_index\00")
  (data $d3 (i32.const 8317) "error reading iterator\00")
  (data $d4 (i32.const 8340) "read\00")
  (data $d5 (i32.const 8345) "object passed to modify is not in multi_index\00")
  (data $d6 (i32.const 8391) "cannot modify objects in table of another contract\00")
  (data $d7 (i32.const 8442) "updater cannot change primary key when modifying an object\00")
  (data $d8 (i32.const 8501) "attempt to add asset with different symbol\00")
  (data $d9 (i32.const 8544) "addition underflow\00")
  (data $d10 (i32.const 8563) "addition overflow\00")
  (data $d11 (i32.const 8581) "write\00")
  (data $d12 (i32.const 8587) "cannot create objects in table of another contract\00")
  (data $d13 (i32.const 8638) "No money.\00")
  (data $d14 (i32.const 8648) "active\00")
  (data $d15 (i32.const 8655) "with draw.\00")
  (data $d16 (i32.const 8666) "cannot pass end iterator to erase\00")
  (data $d17 (i32.const 8700) "cannot increment end iterator\00")
  (data $d18 (i32.const 8730) "object passed to erase is not in multi_index\00")
  (data $d19 (i32.const 8775) "cannot erase objects in table of another contract\00")
  (data $d20 (i32.const 8825) "attempt to remove object that was not in multi_index\00")
  (data $d21 (i32.const 8878) "no check\00")
  (data $d22 (i32.const 8887) "string is too long to be a valid name\00")
  (data $d23 (i32.const 8925) "thirteenth character in name cannot be a letter that comes after j\00")
  (data $d24 (i32.const 8992) "character is not in allowed character set for names\00")
  (data $d25 (i32.const 9044) "get\00")
  (data $d26 (i32.const 0) "X#\00\00"))
