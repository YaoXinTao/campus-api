-- 限流KEY
local key = KEYS[1]
-- 限流大小
local limit = tonumber(ARGV[1])
-- 过期时间
local expire = tonumber(ARGV[2])

-- 获取当前流量大小
local current = tonumber(redis.call('get', key) or "0")

if current + 1 > limit then
    -- 达到限流大小
    return 0
else
    -- 没有达到阈值
    redis.call("INCRBY", key, 1)
    redis.call("EXPIRE", key, expire)
    return current + 1
end 