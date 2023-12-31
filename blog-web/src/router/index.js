import { setSkin } from "@/utils/skin";
import Vue from "vue";
import VueRouter from "vue-router";
import home from '@/view/home/home'

Vue.use(VueRouter);
const routes = [
    {
        path: "/",
        component: home,
        meta: {
            title: "拾壹博客"
        },
        children: [
            {
                path: "/",
                component: resolve => require(["@/view/home/index.vue"], resolve),
                meta: {
                    title: "拾壹博客"
                }
            },
            {

                path: "/links",
                component: resolve => require(["@/view/link/index.vue"], resolve),
                meta: {
                    title: "友情链接"
                }
            },
            {
                path: "/articleInfo",
                component: resolve => require(["@/view/article/index.vue"], resolve),
                meta: {
                    title: "文章详情"
                }
            },
            {
                path: "/message",
                component: resolve => require(["@/view/message/index.vue"], resolve),
                meta: {
                    title: "留言板"
                }
            },
            {
                path: "/about",
                component: resolve => require(["@/view/about/index.vue"], resolve),
                meta: {
                    title: "关于作者"
                }
            },
            {
                path: "/search",
                component: resolve => require(["@/view/search/Search"], resolve),
                meta: {
                    title: "搜索文章"
                }
            },
            {
                path: "/archive",
                component: resolve => require(["@/view/archive/index"], resolve),
                meta: {
                    title: "文章归档"
                }
            },
            {
                path: "/categorys",
                component: resolve => require(["@/view/category/Category.vue"], resolve),
                meta: {
                    title: "文章分类"
                }
            },
            {
                path: "/tag",
                component: resolve => require(["@/view/tag/Tag"], resolve),
            },
            {
                path: "/tags",
                name: "/tags",
                component: resolve => require(["@/view/tag/index"], resolve),
            },
            {
                path: "/photo",
                component: resolve => require(["@/view/photo/index"], resolve),
            },
            {
                path: "/sponsor",
                component: resolve => require(["@/view/sponsor/index"], resolve),
            },
            {
                path: "/im",
                component: resolve => require(["@/view/im/index"], resolve),
            },
            {
                path: "/hot",
                component: resolve => require(["@/view/search/Hot.vue"], resolve),
            },

        ],
    },
    {
        path: "*",
        name: "NotFound",
        component: () => import("@/view/404/404.vue"),
    }
];

const router = new VueRouter({
    mode: "history",
    scrollBehavior: () => ({ y: 0 }),
    routes
});
// 获取原型对象push函数
const originalPush = VueRouter.prototype.push

// 获取原型对象replace函数
const originalReplace = VueRouter.prototype.replace

// 修改原型对象中的push函数
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

// 修改原型对象中的replace函数
VueRouter.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err)
}

router.beforeEach((to, from, next) => {
    setSkin()
    next()
})
export default router;