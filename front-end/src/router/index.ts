// router.js
import { createRouter, createWebHistory } from "vue-router";

import logIn from "../views/logIn.vue";
import dashBoard from "../views/dashBoard.vue";
import signUp from "../views/signUp.vue";
import fy from "../components/fy.vue";
import fl from "../components/fl.vue";
import dm from "../views/dm.vue";
import profile from "../views/profile.vue";
import saved from "../views/saved.vue";
import following from "../components/following.vue";
import follower from "../components/follower.vue";
import ForgotPassword from "../views/ForgetPassword.vue";

const routes = [
  {
    path: "/",
    name: "logIn",
    component: logIn,
  },
  { path: "/forgot-password", component: ForgotPassword },

  {
    path: "/following",
    name: "following",
    component: following,
  },
  {
    path: "/follower",
    name: "follower",
    component: follower,
  },
  {
    path: "/profile",
    name: "profile",
    component: profile,
  },
  {
    path: "/saved",
    name: "saved",
    component: saved,
  },
  {
    path: "/dm",
    name: "dm",
    component: dm,
  },
  {
    path: "/signUp",
    name: "signUp",
    component: signUp,
  },
  {
    path: "/dashBoard",
    name: "dash",
    component: dashBoard,
    children: [
      {
        path: "fl",
        name: "Following",
        component: fl,
      },
      {
        path: "fy",
        name: "ForYou",
        component: fy,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
