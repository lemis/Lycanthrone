/*eslint-disable*/
import React from "react";

import {makeStyles} from "@material-ui/core/styles";
import Hidden from "@material-ui/core/Hidden";

import GridItem from "../../components/Grid/GridItem";
import GridContainer from "../../components/Grid/GridContainer";
import Card from "../../components/Card/Card";
import CardHeader from "../../components/Card/CardHeader";
import CardBody from "../../components/Card/CardBody";

import styles from "./IconsStyle";

const useStyles = makeStyles(styles);

export default function Icons() {
    const classes = useStyles();
    return (
        <GridContainer>
            <GridItem xs={12} sm={12} md={12}>
                <Card plain>
                    <CardHeader plain color="primary">
                        <h4 className={classes.cardTitleWhite}>Material Design Icons</h4>
                        <p className={classes.cardCategoryWhite}>
                            Handcrafted by our friends from{" "}
                            <a target="_blank" href="https://design.google.com/icons/?ref=creativetime">Google</a>
                        </p>
                    </CardHeader>
                    <CardBody>
                        <Hidden only={["sm", "xs"]}>
                            <iframe className={classes.iframe} src="https://material.io/icons/" title="Icons iframe">
                                <p>Your browser does not support iframes.</p>
                            </iframe>
                        </Hidden>
                        <Hidden only={["lg", "md"]}>
                            <GridItem xs={12} sm={12} md={6}>
                                <h5>
                                    The icons are visible on Desktop mode inside an iframe. Since
                                    the iframe is not working on Mobile and Tablets please visit
                                    the icons on their original page on Google. Check the
                                    <a href="https://design.google.com/icons/?ref=creativetime" target="_blank">
                                        Material Icons
                                    </a>
                                </h5>
                            </GridItem>
                        </Hidden>
                    </CardBody>
                </Card>
            </GridItem>
        </GridContainer>
    );
}
