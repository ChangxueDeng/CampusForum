import axios from "axios";
import {ElMessage, ElMessageBox} from "element-plus";
import {router} from "@/router/index.js";

const defaultError = (error)=>{
    console.error(error)
    ElMessage.error("发生了一些错误，请联系管理员")
}

const authItemName = "access_token"

//默认失败回调函数
const defaultFailure = (message, status, url)=>{
    console.warn(`请求地址: ${url}, 状态码: ${status}, 错误信息: ${message}`)
    ElMessage.warning(message)
}



//内部
function internalPost(url, data, header, success, failure, error = defaultError){
    axios.post(url, data,{
        headers: header
    }).then(({data}) =>{
        if(data.status === 200){
            success(data.data)
        }else {
            failure(data.message, data.status, url)
        }
    }).catch(error)
}

//内部使用get
function internalGet(url,header, success, failure, error = defaultError){
    axios.get(url,{
        headers: header
    }).then(({data})=>{
        if(data.status === 200){
            success(data.data)
        }else{
            failure(data.message, data.status, url)
        }
    }).catch(error)
}

//取出token
function takeAccessToken(){
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if(!str) return null
    const authObj = JSON.parse(str)
    if(new Date(authObj.expire) <= new Date()){
        //过期后删除token
        deleteAccessToken()
        ElMessage.info('登陆状态已过期，请重新登陆')
        return null
    }
    return authObj.token
}

//请求头携带token
function accessHeader(){
    const token = takeAccessToken()
    if(token)
        return {"Authorization" : `Bearer ${takeAccessToken()}`}
    return null
}



//保存token
function storeAccessToken(token, remember, expire){
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if(remember){
        localStorage.setItem(authItemName, str)
    }else {
        sessionStorage.setItem(authItemName, str)
    }
}

//删除token
function deleteAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

//外部post

function post(url , data, success, failure = defaultFailure){
    internalPost(url, data,
        // "Content-Type": "application/x-www-form-urlencoded",
        accessHeader()
    , success, failure)
}

//外部get

function get(url, success, failure = defaultFailure){
    internalGet(url,
        // "Content-Type": "application/x-www-form-urlencoded",
        accessHeader()
    , success, failure)
}

function login(username, password, remember, success, failure = defaultFailure){
    internalPost('api/auth/login', {
        username: username,
        password: password,
        remember: remember,
    },{"Content-type":"application/x-www-form-urlencoded"}, (data)=>{
        //判断是否为封禁的账户
        if (!data.ban) {
            storeAccessToken(data.token, remember, data.expire)
            ElMessage.success(`登陆成功, 欢迎 ${data.username}`)
            router.push("/index")
        } else {
            ElMessageBox.alert('账户已被封禁，请联系管理员','提示',{type: "error"})
        }
    }, failure)
}

function logout(success, failure){
    get('api/auth/logout' ,()=>{
        deleteAccessToken()
        ElMessage.success("退出登陆成功")
        success()
    }, failure)
}

function unAuthorized(){
    return !takeAccessToken()
}
export {post, get, login, logout, unAuthorized, accessHeader}