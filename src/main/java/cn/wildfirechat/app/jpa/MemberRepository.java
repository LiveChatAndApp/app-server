package cn.wildfirechat.app.jpa;

import cn.wildfirechat.app.tools.ReflectionUtils;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import com.tencentcloudapi.tci.v20190318.models.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.List;

public interface MemberRepository extends CrudRepository<Member, Long> {


    Member findByUid(String uid);

    Member findByPhone(String phone);

    Member findMemberByMemberName(String memberName);


    @Query("SELECT M FROM Member AS M WHERE M.memberName = :memberInfo OR M.phone = :memberInfo")
    Member findByMemberNameOrPhone(String memberInfo);

    default String getNickname(String uid) {
        Member m = findByUid(uid);
        return m == null ? null : m.getNickName();
    }

    default Member save(InputOutputUserInfo inputOutputUserInfo){
        Member member = findByUid(inputOutputUserInfo.getUserId());
        ReflectionUtils.copyFields(member, inputOutputUserInfo, ReflectionUtils.STRING_TRIM_TO_NULL, true);
        member.setNickName(inputOutputUserInfo.getDisplayName());
        int AvatarIndex = inputOutputUserInfo.getPortrait().indexOf("/avatar/");
        member.setAvatarUrl(inputOutputUserInfo.getPortrait().substring(AvatarIndex));
        member.setGender(inputOutputUserInfo.getGender());
        member.setPhone(inputOutputUserInfo.getMobile());
        member.setSignature(inputOutputUserInfo.getSocial());
        return save(member);
    }

//    @Modifying
//    @Transactional
//    @Query(value = "update Member m set m.password = :password where m.uid = :uid")
//    int updatePassword(@Param("password") String password, @Param("uid") String uid);


    //练习: 使用SQL语法自定义查询, 指定传参名称与否
//    @Query(value = "SELECT _member_name FROM t_member m " +
//            " WHERE m._uid = :uid"
//            , nativeQuery = true)
//    String findMemberUidNative1(@Param("uid") String uid);
//
//    @Query(value = "SELECT _member_name FROM t_member m " +
//            " WHERE m._uid = ?1"
//            , nativeQuery = true)
//    String findMemberUidNative2(String uid);

    //练习: 使用ORM语法自定义查询, 以物件传参
//    @Query("select m.memberName from Member m where m.uid = :#{#member.uid}")
//    String findMemberUid(@Param("member") Member member);



}
