package com.baolong.pictures.infrastructure.manager.redis;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作类
 *
 * @author Baolong 2025年03月06 22:54
 * @version 1.0
 * @since 1.8
 */
@Component

public class RedisCache {
	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 删除单个缓存
	 *
	 * @param key 键
	 * @return Boolean【true 删除成功，false 删除失败】
	 */
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}

	/**
	 * 删除多个缓存
	 *
	 * @param keys 键集合
	 * @return Long【删除键的个数】
	 */
	public Long deletes(Collection<String> keys) {
		return redisTemplate.delete(keys);
	}

	/**
	 * 判断缓存是否存在
	 *
	 * @param key 键
	 * @return Boolean【true 存在，false 不存在】
	 */
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 设置缓存过期时间
	 *
	 * @param key 键
	 * @param et  失效时间，单位毫秒
	 * @return Boolean【true 设置成功，false 设置失败】
	 */
	public Boolean setTime(String key, long et) {
		return redisTemplate.expire(key, et, TimeUnit.MILLISECONDS);
	}

	/**
	 * 设置缓存过期时间
	 *
	 * @param key  键
	 * @param et   失效时间
	 * @param unit 时间单位
	 * @return Boolean【true 设置成功，false 设置失败】
	 */
	public Boolean setTime(String key, long et, TimeUnit unit) {
		return redisTemplate.expire(key, et, unit);
	}

	/**
	 * 获取缓存过期时间
	 *
	 * @param key 键
	 * @return Long【过期时间，单位毫秒】
	 */
	public Long getTime(String key) {
		return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
	}

	/**
	 * 获取缓存过期时间
	 *
	 * @param key  键
	 * @param unit 时间单位
	 * @return Long【过期时间，单位 unit】
	 */
	public Long getTime(String key, TimeUnit unit) {
		return redisTemplate.getExpire(key, unit);
	}

	/**
	 * 删除缓存过期时间
	 *
	 * @param key 键
	 * @return Boolean【true 删除成功，false 删除失败】
	 */
	public Boolean delTime(String key) {
		return redisTemplate.persist(key);
	}

	/**
	 * 获取所有键
	 *
	 * @return Set<String>【慎用】
	 */
	public Set<String> getKeys() {
		return redisTemplate.keys("*");
	}

	/**
	 * 获取所有键
	 *
	 * @param prefix 键前缀
	 * @return Set<String>【键集合】
	 */
	public Set<String> getKeys(String prefix) {
		return redisTemplate.keys(prefix + "*");
	}

	/**
	 * 获取随机一个键
	 *
	 * @return String【随机键】
	 */
	public String getRandomKey() {
		return redisTemplate.randomKey();
	}

	/**
	 * 修改键的名称
	 *
	 * @param oldKey 旧键
	 * @param newKey 新键
	 */
	public void renameKey(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * 获取键的类型
	 *
	 * @param key 键
	 * @return DataType【类型对象】
	 */
	public DataType getType(String key) {
		return redisTemplate.type(key);
	}

	/**
	 * 获取 String 操作对象
	 *
	 * @return ValueOperations<String, String>【键操作对象】
	 */
	public ValueOperations<String, String> opsString() {
		return opsString(null);
	}

	/**
	 * 获取 String 操作对象
	 *
	 * @param redisTemplate 指定 redisTemplate
	 * @return ValueOperations<String, String>【键操作对象】
	 */
	public ValueOperations<String, String> opsString(RedisTemplate<String, String> redisTemplate) {
		if (redisTemplate == null) {
			return this.redisTemplate.opsForValue();
		} else {
			return redisTemplate.opsForValue();
		}
	}

	/**
	 * 获取 Hash 操作对象
	 *
	 * @return HashOperations<String, Object, Object>【键操作对象】
	 */
	public HashOperations<String, String, Object> opsHash() {
		return opsHash(null);
	}

	/**
	 * 获取 Hash 操作对象
	 *
	 * @param redisTemplate 指定 redisTemplate
	 * @return HashOperations<String, Object, Object>【键操作对象】
	 */
	public HashOperations<String, String, Object> opsHash(RedisTemplate<String, String> redisTemplate) {
		if (redisTemplate == null) {
			return this.redisTemplate.opsForHash();
		} else {
			return redisTemplate.opsForHash();
		}
	}

	/**
	 * 获取 List 操作对象
	 *
	 * @return ListOperations<String, String>【键操作对象】
	 */
	public ListOperations<String, String> opsList() {
		return opsList(null);
	}

	/**
	 * 获取 List 操作对象
	 *
	 * @param redisTemplate 指定 redisTemplate
	 * @return ListOperations<String, String>【键操作对象】
	 */
	public ListOperations<String, String> opsList(RedisTemplate<String, String> redisTemplate) {
		if (redisTemplate == null) {
			return this.redisTemplate.opsForList();
		} else {
			return redisTemplate.opsForList();
		}
	}

	/**
	 * 获取 Set 操作对象
	 *
	 * @return SetOperations<String, String>【键操作对象】
	 */
	public SetOperations<String, String> opsSet() {
		return opsSet(null);
	}

	/**
	 * 获取 Set 操作对象
	 *
	 * @param redisTemplate 指定 redisTemplate
	 * @return SetOperations<String, String>【键操作对象】
	 */
	public SetOperations<String, String> opsSet(RedisTemplate<String, String> redisTemplate) {
		if (redisTemplate == null) {
			return this.redisTemplate.opsForSet();
		} else {
			return redisTemplate.opsForSet();
		}
	}

	/**
	 * 获取 ZSet 操作对象
	 *
	 * @return ZSetOperations<String, String>【键操作对象】
	 */
	public ZSetOperations<String, String> opsZSet() {
		return opsZSet(null);
	}

	/**
	 * 获取 ZSet 操作对象
	 *
	 * @param redisTemplate 指定 redisTemplate
	 * @return ZSetOperations<String, String>【键操作对象】
	 */
	public ZSetOperations<String, String> opsZSet(RedisTemplate<String, String> redisTemplate) {
		if (redisTemplate == null) {
			return this.redisTemplate.opsForZSet();
		} else {
			return redisTemplate.opsForZSet();
		}
	}

	// region ========== String 相关操作

	/**
	 * String：获取值
	 *
	 * @param key 键
	 * @return String【值】
	 */
	public String get(String key) {
		return opsString().get(key);
	}

	/**
	 * String：获取值集合
	 *
	 * @param keys 键集合
	 * @return List<String>【值集合】
	 */
	public List<String> gets(Collection<String> keys) {
		return opsString().multiGet(keys);
	}

	/**
	 * String：设置键值
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void set(String key, String value) {
		opsString().set(key, value);
	}

	/**
	 * String：设置键值
	 *
	 * @param key   键
	 * @param value 值
	 * @param et    失效时间，单位毫秒
	 */
	public void set(String key, String value, long et) {
		set(key, value, et, TimeUnit.MILLISECONDS);
	}

	/**
	 * String：设置键值
	 *
	 * @param key   键
	 * @param value 值
	 * @param et    失效时间
	 * @param unit  时间单位
	 */
	public void set(String key, String value, long et, TimeUnit unit) {
		opsString().set(key, value, et, unit);
	}

	/**
	 * String：设置键值集合
	 *
	 * @param map 键值集合
	 */
	public void sets(Map<String, String> map) {
		opsString().multiSet(map);
	}

	/**
	 * String：设置键值，不存在才设置
	 *
	 * @param key   键
	 * @param value 值
	 * @return Boolean【true 不存在设置成功，false 存在设置失败】
	 */
	public Boolean setIfHas(String key, String value) {
		return opsString().setIfAbsent(key, value);
	}

	/**
	 * String：设置键值，不存在才设置
	 *
	 * @param key   键
	 * @param value 值
	 * @param et    失效时间，单位毫秒
	 * @return Boolean【true 不存在设置成功，false 存在设置失败】
	 */
	public Boolean setIfHas(String key, String value, long et) {
		return setIfHas(key, value, et, TimeUnit.MILLISECONDS);
	}

	/**
	 * String：设置键值，不存在才设置
	 *
	 * @param key   键
	 * @param value 值
	 * @param et    失效时间
	 * @param unit  时间单位
	 * @return Boolean【true 不存在设置成功，false 存在设置失败】
	 */
	public Boolean setIfHas(String key, String value, long et, TimeUnit unit) {
		return opsString().setIfAbsent(key, value, et, unit);
	}

	/**
	 * String：设置自增减值
	 *
	 * @param key   键
	 * @param delta 增减值，负数表示自减
	 * @return Long【自增减后的值】
	 */
	public Long incrBy(String key, long delta) {
		return opsString().increment(key, delta);
	}

	// endregion ========== String 相关操作

	// region ========== Hash 相关操作

	/**
	 * Hash：获取值集合
	 *
	 * @param key 键
	 * @return Map<Object, Object>【值集合】
	 */
	public Map<String, Object> hGet(String key) {
		return opsHash().entries(key);
	}

	/**
	 * Hash：获取内容
	 *
	 * @param key   键
	 * @param field 字段
	 * @return Object【内容】
	 */
	public Object hGet(String key, String field) {
		return opsHash().get(key, field);
	}

	/**
	 * Hash：获取值的数量
	 *
	 * @param key 键
	 * @return Long【值的数量】
	 */
	public Long hSize(String key) {
		return opsHash().size(key);
	}

	/**
	 * Hash：设置键值
	 *
	 * @param key   键
	 * @param field 字段
	 * @param value 内容
	 */
	public void hSet(String key, String field, Object value) {
		opsHash().put(key, field, value);
	}

	/**
	 * Hash：设置键值集合
	 *
	 * @param key 键
	 * @param map 值集合
	 */
	public void hSets(String key, Map<String, Object> map) {
		opsHash().putAll(key, map);
	}

	/**
	 * Hash：设置键值，不存在才设置
	 *
	 * @param key   键
	 * @param field 字段
	 * @param value 内容
	 * @return Boolean【true 不存在设置成功，false 存在设置失败】
	 */
	public Boolean setIfHas(String key, String field, Object value) {
		return opsHash().putIfAbsent(key, field, value);
	}

	/**
	 * Hash：判断字段是否存在
	 *
	 * @param key   键
	 * @param field 字段
	 * @return Boolean【true 存在，false 不存在】
	 */
	public Boolean hHas(String key, String field) {
		return opsHash().hasKey(key, field);
	}

	/**
	 * Hash：删除字段
	 *
	 * @param key    键
	 * @param fields 字段，一个或多个
	 * @return Long【删除的数量】
	 */
	public Long hDelete(String key, Object... fields) {
		return opsHash().delete(key, fields);
	}

	/**
	 * Hash：设置字段的自增减值
	 *
	 * @param key   键
	 * @param field 字段
	 * @param delta 增减值，负数表示自减
	 * @return Long【自增减后的值】
	 */
	public Long hIncrBy(String key, String field, long delta) {
		return opsHash().increment(key, field, delta);
	}

	// endregion ========== Hash 相关操作

	// region ========== List 相关操作

	/**
	 * List：获取值
	 *
	 * @param key   键
	 * @param index 索引
	 * @return String【值】
	 */
	public String lGet(String key, long index) {
		return opsList().index(key, index);
	}

	/**
	 * List：获取值集合
	 *
	 * @param key 键
	 * @return List<String>【值集合】
	 */
	public List<String> lGets(String key) {
		return opsList().range(key, 0, -1);
	}

	/**
	 * List：获取值集合
	 *
	 * @param key   键
	 * @param start 开始位置
	 * @param end   结束位置，-1 表示全部
	 * @return List<String>【值集合】
	 */
	public List<String> lGets(String key, int start, int end) {
		return opsList().range(key, start, end);
	}

	/**
	 * List：获取值数量
	 *
	 * @param key 键
	 * @return Long【值数量】
	 */
	public Long lSize(String key) {
		return opsList().size(key);
	}

	/**
	 * List：设置值，从左边
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void lSetLeft(String key, String value) {
		opsList().leftPush(key, value);
	}

	/**
	 * List：设置值集合，从左边
	 *
	 * @param key    键
	 * @param values 值集合
	 */
	public void lSetsLeft(String key, String... values) {
		opsList().leftPushAll(key, values);
	}

	/**
	 * List：设置值集合，从左边
	 *
	 * @param key    键
	 * @param values 值集合
	 */
	public void lSetsLeft(String key, Collection<String> values) {
		opsList().leftPushAll(key, values);
	}

	/**
	 * List：设置值，从右边
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void lSetRight(String key, String value) {
		opsList().rightPush(key, value);
	}

	/**
	 * List：设置值集合，从右边
	 *
	 * @param key    键
	 * @param values 值集合
	 */
	public void lSetsRight(String key, String... values) {
		opsList().rightPushAll(key, values);
	}

	/**
	 * List：设置值集合，从右边
	 *
	 * @param key    键
	 * @param values 值集合
	 */
	public void lSetsRight(String key, Collection<String> values) {
		opsList().rightPushAll(key, values);
	}

	/**
	 * List：设置值，从指定位置
	 *
	 * @param key   键
	 * @param index 索引位置
	 * @param value 值
	 */
	public void lSetIndex(String key, long index, String value) {
		opsList().set(key, index, value);
	}

	/**
	 * List：移除值
	 *
	 * @param key   键
	 * @param value 值
	 * @param count 数量
	 * @return Long【移除的数量】
	 */
	public Long lRemove(String key, String value, long count) {
		return opsList().remove(key, count, value);
	}

	// endregion ========== List 相关操作

	// region ========== Set 相关操作

	/**
	 * Set：获取值集合
	 *
	 * @param key 键
	 * @return Set<String>【值集合】
	 */
	public Set<String> sGet(String key) {
		return opsSet().members(key);
	}

	/**
	 * Set：获取值，随机获取
	 *
	 * @param key 键
	 * @return String【值】
	 */
	public String sGetRandom(String key) {
		return opsSet().randomMember(key);
	}

	/**
	 * Set：获取值集合，随机获取指定个数
	 *
	 * @param key   键
	 * @param count 获取的个数
	 * @return List<String>【值集合】
	 */
	public List<String> sGetRandom(String key, long count) {
		return opsSet().randomMembers(key, count);
	}

	/**
	 * Set：获取值数量
	 *
	 * @param key 键
	 * @return Long【值数量】
	 */
	public Long sSize(String key) {
		return opsSet().size(key);
	}

	/**
	 * Set：设置键值
	 *
	 * @param key   键
	 * @param value 值
	 * @return Long【0 设置失败，1 设置成功】
	 */
	public Long sSet(String key, String value) {
		return opsSet().add(key, value);
	}

	/**
	 * Set：设置键值集合
	 *
	 * @param key    键
	 * @param values 值
	 * @return Long【0 设置失败，1 设置成功】
	 */
	public Long sSets(String key, String... values) {
		return opsSet().add(key, values);
	}

	/**
	 * Set：移除值
	 *
	 * @param key    键
	 * @param values 值，可以删除多个
	 * @return Long【移除的数量】
	 */
	public Long sRemove(String key, Object... values) {
		return opsSet().remove(key, values);
	}

	/**
	 * Set：判断值是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return Boolean【true 存在，false 不存在】
	 */
	public Boolean sHas(String key, String value) {
		return opsSet().isMember(key, value);
	}

	/**
	 * Set：获取两个集合的交集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【交集】
	 */
	public Set<String> sGetIntersect(String key1, String key2) {
		return opsSet().intersect(key1, key2);
	}

	/**
	 * Set：获取多个集合的交集
	 *
	 * @param keys 键集合
	 * @return Set<String>【交集】
	 */
	public Set<String> sGetIntersect(Collection<String> keys) {
		return opsSet().intersect(keys);
	}

	/**
	 * Set：获取两个集合的并集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【并集】
	 */
	public Set<String> sGetUnion(String key1, String key2) {
		return opsSet().union(key1, key2);
	}

	/**
	 * Set：获取多个集合的并集
	 *
	 * @param keys 键集合
	 * @return Set<String>【并集】
	 */
	public Set<String> sGetUnion(Collection<String> keys) {
		return opsSet().union(keys);
	}

	/**
	 * Set：获取两个集合的差集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【差集】
	 */
	public Set<String> sGetDifference(String key1, String key2) {
		return opsSet().difference(key1, key2);
	}

	/**
	 * Set：获取多个集合的差集
	 *
	 * @param keys 键集合
	 * @return Set<String>【差集】
	 */
	public Set<String> sGetDifference(Collection<String> keys) {
		return opsSet().difference(keys);
	}

	// endregion ========== Set 相关操作

	// region ========== ZSet 相关操作

	/**
	 * ZSet：获取值集合，分数小的在前面
	 *
	 * @param key 键
	 * @return Set<String>【值集合】
	 */
	public Set<String> zGet(String key) {
		return opsZSet().range(key, 0, -1);
	}

	/**
	 * ZSet：获取值集合，分数小的在前面
	 *
	 * @param key   键
	 * @param start 开始位置
	 * @param end   结束位置，-1 表示全部
	 * @return Set<String>【值集合】
	 */
	public Set<String> zGet(String key, int start, int end) {
		return opsZSet().range(key, start, end);
	}

	/**
	 * ZSet：获取值集合，分数大的在前面
	 *
	 * @param key 键
	 * @return Set<String>【值集合】
	 */
	public Set<String> zGetDesc(String key) {
		return opsZSet().reverseRange(key, 0, -1);
	}

	/**
	 * ZSet：获取值集合，分数大的在前面
	 *
	 * @param key   键
	 * @param start 开始位置
	 * @param end   结束位置，-1 表示全部
	 * @return Set<String>【值集合】
	 */
	public Set<String> zGetDesc(String key, int start, int end) {
		return opsZSet().reverseRange(key, start, end);
	}

	/**
	 * ZSet：获取值集合，分数在 min 和 max 之间的值
	 *
	 * @param key 键
	 * @param min 最小值
	 * @param max 最大值
	 * @return Set<String>【值集合】
	 */
	public Set<String> zGet(String key, double min, double max) {
		return opsZSet().rangeByScore(key, min, max);
	}

	/**
	 * ZSet：获取值排名，分数小的排名考前
	 *
	 * @param key   键
	 * @param value 值
	 * @return Long【排名】
	 */
	public Long zGetRank(String key, String value) {
		return opsZSet().rank(key, value);
	}

	/**
	 * ZSet：获取值排名，分数大的排名考前
	 *
	 * @param key   键
	 * @param value 值
	 * @return Long【排名】
	 */
	public Long zGetRankDesc(String key, String value) {
		return opsZSet().reverseRank(key, value);
	}

	/**
	 * ZSet：获取值分数
	 *
	 * @param key   键
	 * @param value 值
	 * @return Double【分数】
	 */
	public Double zGetScore(String key, String value) {
		return opsZSet().score(key, value);
	}

	/**
	 * ZSet：获取值分数集合
	 *
	 * @param key 键
	 * @return Set<ZSetOperations.TypedTuple < String>>【值分数集合】
	 */
	public Set<ZSetOperations.TypedTuple<String>> zGetWithScores(String key) {
		return opsZSet().rangeWithScores(key, 0, -1);
	}

	/**
	 * ZSet：获取值分数集合
	 *
	 * @param key   键
	 * @param start 开始位置
	 * @param end   结束位置，-1 表示全部
	 * @return Set<ZSetOperations.TypedTuple < String>>【值分数集合】
	 */
	public Set<ZSetOperations.TypedTuple<String>> zGetWithScores(String key, int start, int end) {
		return opsZSet().rangeWithScores(key, start, end);
	}

	/**
	 * ZSet：获取值数量
	 *
	 * @param key 键
	 * @return Long【值数量】
	 */
	public Long zSize(String key) {
		return opsZSet().size(key);
	}

	/**
	 * ZSet：获取值数量
	 *
	 * @param key 键
	 * @return Long【值数量】
	 */
	public Long zCard(String key) {
		return opsZSet().zCard(key);
	}

	/**
	 * ZSet：移除值
	 *
	 * @param key    键
	 * @param values 值，可以删除多个
	 * @return Long【移除的数量】
	 */
	public Long ZRemove(String key, Object... values) {
		return opsZSet().remove(key, values);
	}

	/**
	 * ZSet：获取两个集合的交集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【交集】
	 */
	public Set<String> zGetIntersect(String key1, String key2) {
		return opsZSet().intersect(key1, key2);
	}

	/**
	 * ZSet：获取一个集合和多个集合的交集
	 *
	 * @param keys 键集合
	 * @return Set<String>【交集】
	 */
	public Set<String> zGetIntersect(String key, Collection<String> keys) {
		return opsZSet().intersect(key, keys);
	}

	/**
	 * ZSet：获取两个集合的并集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【并集】
	 */
	public Set<String> zGetUnion(String key1, String key2) {
		return opsZSet().union(key1, key2);
	}

	/**
	 * ZSet：获取一个集合和多个集合的并集
	 *
	 * @param keys 键集合
	 * @return Set<String>【并集】
	 */
	public Set<String> zGetUnion(String key, Collection<String> keys) {
		return opsZSet().union(key, keys);
	}

	/**
	 * ZSet：获取两个集合的差集
	 *
	 * @param key1 键1
	 * @param key2 键2
	 * @return Set<String>【差集】
	 */
	public Set<String> zGetDifference(String key1, String key2) {
		return opsZSet().difference(key1, key2);
	}

	/**
	 * ZSet：获取一个集合和多个集合的差集
	 *
	 * @param keys 键集合
	 * @return Set<String>【差集】
	 */
	public Set<String> zGetDifference(String key, Collection<String> keys) {
		return opsZSet().difference(key, keys);
	}

	// endregion ========== ZSet 相关操作
}
