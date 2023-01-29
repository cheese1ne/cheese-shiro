--周期开始时间
local curr_mill_second= tonumber(ARGV[1])
--令牌消耗数量
local consume_permits = tonumber(ARGV[2])
--缓存过期时间
local exp_seconds = tonumber(ARGV[3])
--令牌桶最大数量
local max_burst = tonumber(ARGV[4])

-- 查看是否已经存在缓存
local info = redis.pcall("EXISTS",KEYS[1])
-- 没有缓存或已过期 初始化
if(0 == tonumber(info)) then
    -- 当前令牌初始化为桶最大数量
    local curr_permits= max_burst
    local result = 0
    -- 数量充足
    if (max_burst >= consume_permits)  then
        curr_permits = max_burst -consume_permits
        result =1
    end
    redis.pcall("HMSET",KEYS[1],
            "last_mill_second",curr_mill_second,
            "curr_permits",curr_permits)
    -- 设置过期时间
    redis.pcall("EXPIRE",KEYS[1],exp_seconds)
    return result
else
    -- 存在缓存
    local ratelimit_info=redis.pcall("HMGET",KEYS[1],"last_mill_second","curr_permits")
    -- 上次日期
    local last_mill_second= tonumber(ratelimit_info[1])
    -- 当前剩余令牌数量
    local curr_permits= tonumber(ratelimit_info[2])
    -- 剩余令牌充足时，只重置令牌数量，依靠缓存过期进行数量校正，减少步骤
    -- 剩余令牌不足时，重新计算令牌数量进行判断，产生新的新牌后，重置令牌数量，过期时间
    if (curr_permits >= consume_permits) then
        -- 数量足够,直接重置令牌数量
        redis.pcall("HMSET",KEYS[1],"curr_permits",curr_permits-consume_permits)
        return 1
    else
        -- 数量不足
        -- 生产速率
        local rate = tonumber(ARGV[5])
        -- 产生的令牌数量
        local reverse_permits=math.floor((curr_mill_second-last_mill_second)/1000)*rate
        local result=0
        -- 有新的令牌产生时，再进行计算，否则数量还是不够
        if (reverse_permits > 0) then
            -- 期望的令牌数量
            local expect_curr_permits=reverse_permits+curr_permits
            -- 与令牌桶进行比较，取最小值，获取当前令牌数量
            local local_curr_permits=math.min(expect_curr_permits,max_burst);
            -- 数量判断，重置令牌数量
            if(local_curr_permits-consume_permits>=0) then
                result=1
                redis.pcall("HMSET",KEYS[1],"curr_permits",local_curr_permits-consume_permits)
            else
                redis.pcall("HMSET",KEYS[1],"curr_permits",local_curr_permits)
            end
            -- 重置周期时间
            redis.pcall("HMSET",KEYS[1],"last_mill_second",curr_mill_second)
            -- 重置过期时间
            redis.pcall("EXPIRE",KEYS[1],exp_seconds)
        end
        return result
    end

end
