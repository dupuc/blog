<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on"
      label-position="left">

      <div class="title-container">
        <h3 class="title">拾壹博客后台管理系统</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input v-model="loginForm.username" placeholder="用户名" name="username" type="text" tabindex="1"
          auto-complete="on" />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input :key="passwordType" v-model="loginForm.password" :type="passwordType" placeholder="密码" name="password"
          tabindex="2" auto-complete="on" ref="password" @keyup.enter.native="handleLogin" />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>
      <!-- <el-form-item prop="code">
        <span class="svg-container">
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </span>
        <el-input prop="code" v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%"
          @keyup.enter.native="handleLogin">
        </el-input>
        <div class="login-code">
          <img :src="changeImage" @click="handleClickImage" class="login-code-img" />
        </div>
      </el-form-item> -->
      <!--滑块验证-->
      <el-dialog title="请拖动滑块完成拼图" width="360px" :visible.sync="isShowSliderVerify" :close-on-click-modal="false"
        @closed="refresh" append-to-body>
        <slider-verify ref="sliderVerify" @success="onSuccess" @fail="onFail" @again="onAgain" />
      </el-dialog>

      <div style="margin-bottom: 10px;margin-left: 2px">
        <el-checkbox v-model="loginForm.rememberMe">
          <span style="color: white">记住我</span>
        </el-checkbox>
      </div>
      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
        @click.native.prevent="handleLogin">
        <span v-if="!loading">登 录</span>
        <span v-else>登 录 中...</span>
      </el-button>

      <div style="position:relative">
        <div class="tips">
          <span>Username : test</span>
          <span>Password : 123456</span>
          <el-button style="float: right" class="thirdparty-button" type="primary" @click="showDialog = true">
            第三方登录
          </el-button>
        </div>
      </div>
    </el-form>
    <!--引入粒子特效-->
    <vue-particles color="#fff" :particleOpacity="0.7" :particlesNumber="60" shapeType="circle" :particleSize="4"
      linesColor="#fff" :linesWidth="1" :lineLinked="true" :lineOpacity="0.4" :linesDistance="150" :moveSpeed="2"
      :hoverEffect="true" hoverMode="grab" :clickEffect="true" clickMode="push" class="lizi">
    </vue-particles>
    <el-dialog :modal-append-to-body="false" title="第三方登录" :visible.sync="showDialog">
      本地不能模拟，请结合自己业务进行模拟！！！
      <br>
      <br>
      <br>
      <social-sign />
    </el-dialog>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2022 <a href="http://www.shiyit.com" target="_blank">shiyit.com</a> All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import sliderVerify from './components/sliderVerify';
import { validUsername } from '@/utils/validate'
import { login } from '@/api/user'
import SocialSign from './components/SocialSignin'
export default {
  name: 'Login',
  components: { SocialSign, sliderVerify },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('用户名由字母组成并且至少3位'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能小于6位数'))
      } else {
        callback()
      }
    }
    return {
      // 随机字符串
      nonceStr: '',
      // 验证值
      value: '',
      // 是否显示滑块验证
      isShowSliderVerify: false,
      changeImage: '',
      showDialog: false,
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        uuid: null,
        code: null,
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        code: [{ required: true, trigger: 'blur', message: '请输入验证码', }],
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.keyupEnter()
  },
  methods: {
    /* 提交*/
    handleLogin() {
      let self = this;
      self.$refs.loginForm.validate((flag) => {
        self.isShowSliderVerify = flag;
      });
    },
    /* 滑动验证成功*/
    onSuccess(captcha) {
      Object.assign(this.loginForm, captcha);
      this.loginForm.nonceStr = captcha.nonceStr
      this.loginForm.value = captcha.value
      this.login();
    },
    /* 滑动验证失败*/
    onFail(msg) {
      console.log(msg)
      //this.message('error', msg || '验证失败，请控制拼图对齐缺口');
    },
    /* 滑动验证异常*/
    onAgain() {
      this.$message.error('滑动操作异常，请重试');
    },
    /* 刷新验证码*/
    refresh() {
      this.$refs.sliderVerify.refresh();
    },
    /* 提示弹框*/
    message(type, message) {
      this.$message({
        showClose: true,
        type: type,
        message: message,
        duration: 1500,
      });
    },
    keyupEnter() {
      document.onkeydown = e => {
        let _key = window.event.keyCode;
        if (_key === 13) {
          this.handleLogin()
        }
      }
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    login() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          let that = this;
          that.loading = true
          login(that.loginForm).then(res => {
            let data = { token: res.data, rememberMe: that.loginForm.rememberMe }
            that.$store.dispatch('user/login', data).then(res => {
              that.$refs.sliderVerify.verifySuccessEvent();
              setTimeout(() => {
                that.isShowSliderVerify = false;
                that.$message.success('登录成功');
              }, 500);
              that.loading = false
              that.$router.push('/')
            })
          }).catch(err => {
            that.loading = false
            that.$refs.sliderVerify.verifyFailEvent();
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss">
$bg: #2d3a4b;
$light_gray: #eee;

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 38px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;

      &:-webkit-autofill {
        -webkit-box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: #fff !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }

  .login-code {
    height: 38px;
    float: right;

    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }

  .login-code-img {
    height: 40px;
  }

}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: $bg;

  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;

    .el-input {
      height: 38px;

      input {
        height: 38px;
      }
    }
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 0px 6px 0px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;

    &_login {
      font-size: 20px;
    }
  }

  .title {
    font-size: 26px;
    font-weight: 400;
    color: $light_gray;
    margin: 0px auto 40px auto;
    text-align: center;
    font-weight: bold;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .el-login-footer {
    height: 40px;
    line-height: 40px;
    position: fixed;
    bottom: 0;
    width: 100%;
    text-align: center;
    color: #fff;
    font-family: Arial;
    font-size: 12px;
    letter-spacing: 1px;
  }
}
</style>

