import React from "react";
import PropTypes from "prop-types";
import {makeStyles, Typography} from "@material-ui/core";
import {TreeItem, TreeView} from "@material-ui/lab";

import MailIcon from "@material-ui/icons";
import DeleteIcon from "@material-ui/icons/Delete";
import Label from "@material-ui/icons/Label";
import SupervisorAccountIcon from "@material-ui/icons/SupervisorAccount";
import InfoIcon from "@material-ui/icons/Info";
import ForumIcon from "@material-ui/icons/Forum";
import LocalOfferIcon from "@material-ui/icons/LocalOffer";
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import ArrowRightIcon from "@material-ui/icons/ArrowRight";

ReactDOM.render(<GmailTreeView/>, document.getElementById("root"));

const useTreeItemStyles = makeStyles(theme => ({
    root: {
        color: theme.palette.text.secondary,
        "&:focus > $content": {
            backgroundColor: `var(--tree-view-bg-color, ${theme.palette.grey[400]})`,
            color: "var(--tree-view-color)"
        }
    },
    content: {
        color: theme.palette.text.secondary,
        borderTopRightRadius: theme.spacing(2),
        borderBottomRightRadius: theme.spacing(2),
        paddingRight: theme.spacing(1),
        fontWeight: theme.typography.fontWeightMedium,
        "$expanded > &": {
            fontWeight: theme.typography.fontWeightRegular
        }
    },
    group: {
        marginLeft: 0,
        "& $content": {
            paddingLeft: theme.spacing(2)
        }
    },
    expanded: {},
    label: {
        fontWeight: "inherit",
        color: "inherit"
    },
    labelRoot: {
        display: "flex",
        alignItems: "center",
        padding: theme.spacing(0.5, 0)
    },
    labelIcon: {
        marginRight: theme.spacing(1)
    },
    labelText: {
        fontWeight: "inherit",
        flexGrow: 1
    }
}));

class StyledTreeItem extends React.Component {
    render() {
        const classes = useTreeItemStyles();
        const {labelText, labelIcon: LabelIcon, labelInfo, color, bgColor, ...other} = this.props;

        const treeLabel = (
            <div className={classes.labelRoot}>
                <LabelIcon color="inherit" className={classes.labelIcon}/>
                <Typography variant="body2" className={classes.labelText}>{labelText}</Typography>
                <Typography variant="caption" color="inherit">{labelInfo}</Typography>
            </div>
        );

        const {root, content, expanded, group, label} = classes;

        return (
            <TreeItem
                label={treeLabel}
                style={{"--tree-view-color": color, "--tree-view-bg-color": bgColor}}
                classes={{root, content, expanded, group, label}}
                {...other}
            />
        );
    }
}

StyledTreeItem.propTypes = {
    bgColor: PropTypes.string,
    color: PropTypes.string,
    labelIcon: PropTypes.elementType.isRequired,
    labelInfo: PropTypes.string,
    labelText: PropTypes.string.isRequired
};

const useStyles = makeStyles({
    root: {
        height: 264,
        flexGrow: 1,
        maxWidth: 400
    }
});

// export default
class GmailTreeView extends React.Component {
    render() {
        const classes = useStyles();

        return (
            <TreeView
                className={classes.root}
                defaultExpanded={["3"]}
                defaultCollapseIcon={<ArrowDropDownIcon/>}
                defaultExpandIcon={<ArrowRightIcon/>}
                defaultEndIcon={<div style={{width: 24}}/>}>

                <StyledTreeItem nodeId="1" labelText="All Mail" labelIcon={MailIcon}/>
                <StyledTreeItem nodeId="2" labelText="Trash" labelIcon={DeleteIcon}/>
                <StyledTreeItem nodeId="3" labelText="Categories" labelIcon={Label}>
                    <StyledTreeItem
                        nodeId="5" labelText="Social" labelIcon={SupervisorAccountIcon}
                        labelInfo="90" color="#1a73e8" bgColor="#e8f0fe"/>

                    <StyledTreeItem
                        nodeId="6" labelText="Updates" labelIcon={InfoIcon}
                        labelInfo="2,294" color="#e3742f" bgColor="#fcefe3"/>

                    <StyledTreeItem
                        nodeId="7" labelText="Forums" labelIcon={ForumIcon}
                        labelInfo="3,566" color="#a250f5" bgColor="#f3e8fd"/>

                    <StyledTreeItem
                        nodeId="8" labelText="Promotions" labelIcon={LocalOfferIcon}
                        labelInfo="733" color="#3c8039" bgColor="#e6f4ea"/>

                </StyledTreeItem>
                <StyledTreeItem nodeId="4" labelText="History" labelIcon={Label}/>
            </TreeView>
        );
    }
}
