import React from "react";
import {Redirect, Route, Switch} from "react-router-dom";

import PerfectScrollbar from "perfect-scrollbar";

import {makeStyles} from "@material-ui/core/styles";

import Navbar from "../components/Navbars/Navbar";
import Footer from "../components/Footer/Footer";
import Sidebar from "../components/Sidebar/Sidebar";
import FixedPlugin from "../components/FixedPlugin/FixedPlugin";

import routes from "../routes";

import styles from "./AdminStyle";

import bgImage from "../assets/img/sidebar-2.jpg";
import logo from "../assets/img/reactlogo.png";

let ps;

const switchRoutes = (
    <Switch>
        {routes.map((prop, key) => {
            return prop.layout === "/admin"
                ? <Route path={prop.layout + prop.path} component={prop.component} key={key}/>
                : null;
        })}
        <Redirect from="/admin" to="/admin/dashboard"/>
    </Switch>
);

const useStyles = makeStyles(styles);

export default function Admin({...rest}) {
    // styles
    const classes = useStyles();
    // ref to help us initialize PerfectScrollbar on windows devices
    const mainPanel = React.createRef();
    // states and functions
    const [image, setImage] = React.useState(bgImage);
    const [color, setColor] = React.useState("blue");
    const [fixedClasses, setFixedClasses] = React.useState("dropdown show");
    const [mobileOpen, setMobileOpen] = React.useState(false);

    const handleImageClick = image => setImage(image);
    const handleColorClick = color => setColor(color);

    const handleFixedClick = () => setFixedClasses("dropdown" + (fixedClasses === "dropdown" ? " show" : ""));
    const handleDrawerToggle = () => setMobileOpen(!mobileOpen);
    const getRoute = () => window.location.pathname !== "/admin/maps";
    const resizeFunction = () => {
        if (window.innerWidth >= 960) {
            setMobileOpen(false);
        }
    };

    // initialize and destroy the PerfectScrollbar plugin
    React.useEffect(() => {
        if (navigator.platform.indexOf("Win") > -1) {
            ps = new PerfectScrollbar(mainPanel.current, {
                suppressScrollX: true,
                suppressScrollY: false
            });
            document.body.style.overflow = "hidden";
        }
        window.addEventListener("resize", resizeFunction);
        // Specify how to clean up after this effect:
        return () => {
            if (navigator.platform.indexOf("Win") > -1) ps.destroy();
            window.removeEventListener("resize", resizeFunction);
        };
    }, [mainPanel]);
    return (
        <div className={classes.wrapper}>
            <Sidebar
                routes={routes}
                logoText={"Creative Tim"}
                logo={logo}
                image={image}
                handleDrawerToggle={handleDrawerToggle}
                open={mobileOpen}
                color={color}
                {...rest}/>
            <div className={classes.mainPanel} ref={mainPanel}>
                <Navbar routes={routes} handleDrawerToggle={handleDrawerToggle} {...rest}/>
                {/* On the /maps route we want the map to be on full screen - this is not possible if the content and container classes are present because they have some padding which would make the map smaller */}
                {getRoute() ? (
                    <div className={classes.content}>
                        <div className={classes.container}>{switchRoutes}</div>
                    </div>
                ) : (
                    <div className={classes.map}>{switchRoutes}</div>
                )}
                {getRoute() && <Footer/>}
                <FixedPlugin bgColor={color} bgImage={image} fixedClasses={fixedClasses}
                    handleImageClick={handleImageClick}
                    handleColorClick={handleColorClick}
                    handleFixedClick={handleFixedClick}/>
            </div>
        </div>
    );
}
